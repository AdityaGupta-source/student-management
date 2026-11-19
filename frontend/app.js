// Frontend logic: calls the Spring Boot REST API and shows the results.
// Change this one line if the backend runs on a different port.
const API_BASE = "http://localhost:8080";
const STUDENTS_URL = API_BASE + "/api/students";

let currentTab = "all";
let shownStudents = [];      // the list currently in the table
let rankMode = false;        // true on the merit list: keep server order and show ranks
let sortKey = null;
let sortDir = 1;             // 1 = ascending, -1 = descending

// ---------- small helpers ----------

function $(id) {
  return document.getElementById(id);
}

// Escapes text before putting it in HTML, so user input cannot inject markup.
function esc(value) {
  const div = document.createElement("div");
  div.textContent = value == null ? "" : String(value);
  return div.innerHTML;
}

// Pop-up message at the bottom right, hidden after 4 seconds.
function toast(text, type) {
  const box = document.createElement("div");
  box.className = "toast " + (type || "success");
  box.textContent = text;
  $("toasts").appendChild(box);
  setTimeout(() => box.remove(), 4000);
}

// Turns "Java, SQL, " into ["Java", "SQL"].
function parseSkills(text) {
  return text.split(",").map((s) => s.trim()).filter((s) => s !== "");
}

// Calls the API and returns JSON. On an error status, throws the server's message.
async function callApi(url, options) {
  let response;
  try {
    response = await fetch(url, options);
  } catch (e) {
    throw new Error("Cannot reach the backend at " + API_BASE + ". Is it running?");
  }
  const data = await response.json().catch(() => null);
  if (!response.ok) {
    throw new Error(buildErrorText(data, response.status));
  }
  return data;
}

function buildErrorText(data, status) {
  if (!data) {
    return "Request failed (" + status + ")";
  }
  let text = data.message || "Request failed (" + status + ")";
  if (data.fieldErrors) {
    text += ": " + Object.values(data.fieldErrors).join(", ");
  }
  return text;
}

function jsonOptions(method, body) {
  return { method: method, headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) };
}

// ---------- building the table ----------

const AVATAR_COLORS = ["#4f46e5", "#0891b2", "#16a34a", "#d97706", "#db2777", "#7c3aed", "#0d9488"];

function initials(name) {
  return name.split(" ").filter((p) => p !== "").slice(0, 2).map((p) => p[0].toUpperCase()).join("");
}

// The same name always gets the same colour.
function avatarColor(name) {
  let sum = 0;
  for (const ch of name) {
    sum += ch.charCodeAt(0);
  }
  return AVATAR_COLORS[sum % AVATAR_COLORS.length];
}

function scoreLevel(score) {
  if (score >= 70) return "high";
  if (score >= 50) return "mid";
  return "low";
}

function scoreCell(score) {
  return '<div class="score"><span>' + score + '</span><div class="bar"><i class="' + scoreLevel(score) +
    '" style="width:' + Math.min(Math.max(score, 0), 100) + '%"></i></div></div>';
}

function rankCell(index) {
  const medals = ["🥇", "🥈", "🥉"];
  return '<td class="rank">' + (medals[index] || index + 1) + "</td>";
}

function studentRow(s, index) {
  const skills = s.technicalSkills.map((k) => '<span class="chip">' + esc(k) + "</span>").join("");
  const id = esc(s.studentId);
  return "<tr>" +
    (rankMode ? rankCell(index) : "") +
    '<td><div class="person"><div class="avatar" style="background:' + avatarColor(s.studentName) + '">' +
      esc(initials(s.studentName)) + "</div><div><strong>" + esc(s.studentName) + "</strong><small>" +
      id + " &middot; " + esc(s.email) + "</small></div></div></td>" +
    "<td>" + esc(s.courseName) + "</td>" +
    "<td>" + scoreCell(s.programmingScore) + "</td>" +
    "<td>" + scoreCell(s.mathematicsScore) + "</td>" +
    '<td class="avg"><strong>' + s.average.toFixed(2) + '</strong><br><span class="badge ' +
      s.category.replace(/ /g, "") + '">' + esc(s.category) + "</span><small>Stronger: " +
      esc(s.strongerSubject) + "</small></td>" +
    "<td>" + (skills || '<small class="count">none</small>') + "</td>" +
    '<td class="actions"><button class="small ghost" data-edit="' + id + '">Edit</button> ' +
      '<button class="small danger" data-delete="' + id + '">Delete</button></td></tr>';
}

function sortedCopy(list) {
  const copy = list.slice();
  if (!sortKey || rankMode) {
    return copy;
  }
  copy.sort((a, b) => {
    const x = a[sortKey], y = b[sortKey];
    const result = typeof x === "string" ? x.localeCompare(y) : x - y;
    return result * sortDir;
  });
  return copy;
}

function updateSortHeaders() {
  document.querySelectorAll("th.sortable").forEach((th) => {
    th.classList.remove("sorted-asc", "sorted-desc");
    if (!rankMode && th.dataset.sort === sortKey) {
      th.classList.add(sortDir === 1 ? "sorted-asc" : "sorted-desc");
    }
  });
}

function renderTable() {
  const rows = sortedCopy(shownStudents);
  $("studentRows").innerHTML = rows.map(studentRow).join("");
  $("rankHead").classList.toggle("hidden", !rankMode);
  $("emptyText").classList.toggle("hidden", rows.length > 0);
  $("listCount").textContent = rows.length + (rows.length === 1 ? " student" : " students");
  updateSortHeaders();
}

function showStudents(students, title, withRank) {
  shownStudents = students;
  rankMode = Boolean(withRank);
  $("listTitle").textContent = title;
  renderTable();
}

function showLoading() {
  $("emptyText").classList.add("hidden");
  $("studentRows").innerHTML = '<tr><td colspan="8" class="loading">Loading...</td></tr>';
}

// ---------- summary cards ----------

function updateStats(students) {
  $("statTotal").textContent = students.length;
  if (students.length === 0) {
    $("statAverage").textContent = $("statTop").textContent = "-";
    return;
  }
  let total = 0;
  let top = students[0];
  for (const s of students) {
    total += s.average;
    if (s.average > top.average) top = s;
  }
  $("statAverage").textContent = (total / students.length).toFixed(1);
  $("statTop").textContent = top.studentName + " (" + top.average.toFixed(1) + ")";
}

// ---------- loading each tab ----------

async function loadAll() {
  showLoading();
  const students = await callApi(STUDENTS_URL);
  updateStats(students);
  showStudents(students, "All Students", false);
}

async function loadMerit() {
  showLoading();
  showStudents(await callApi(STUDENTS_URL + "/merit-list"), "Merit List", true);
}

async function loadDocs() {
  const docs = await callApi(API_BASE + "/api/docs");
  $("docsUrl").textContent = API_BASE + "/api/docs";
  $("docsList").innerHTML = docs.map((d) =>
    '<div class="doc"><div class="doc-line"><span class="method ' + esc(d.method) + '">' + esc(d.method) +
    "</span><code>" + esc(d.path) + "</code></div>" +
    "<p>" + esc(d.description) + " &middot; Status codes: " + esc(d.statusCodes) + "</p>" +
    (d.sampleBody !== "-" ? "<pre>" + esc(d.sampleBody) + "</pre>" : "") + "</div>"
  ).join("");
}

// ---------- tabs ----------

const tabLoaders = { all: loadAll, merit: loadMerit, docs: loadDocs };

function showTab(tab) {
  currentTab = tab;
  document.querySelectorAll("#tabs button").forEach((b) => b.classList.toggle("active", b.dataset.tab === tab));
  const listTabs = ["all", "merit"];
  $("listView").classList.toggle("hidden", !listTabs.includes(tab));
  $("statsView").classList.toggle("hidden", tab !== "all");
  $("searchBar").classList.toggle("hidden", tab !== "all");
  $("addView").classList.toggle("hidden", tab !== "add");
  $("docsView").classList.toggle("hidden", tab !== "docs");
  refreshCurrentTab();
}

async function refreshCurrentTab() {
  const loader = tabLoaders[currentTab];
  if (!loader) {
    return;
  }
  try {
    await loader();
  } catch (e) {
    showStudents([], $("listTitle").textContent, false);
    toast(e.message, "error");
  }
}

// ---------- search ----------

async function searchStudents() {
  const value = $("searchValue").value.trim();
  if (value === "") {
    toast("Please type a student ID to search.", "error");
    return;
  }
  const title = "Search result: ID = " + value;
  try {
    const student = await callApi(STUDENTS_URL + "/" + encodeURIComponent(value));
    showStudents([student], title, false);
  } catch (e) {
    showStudents([], title, false);
    toast(e.message, "error");
  }
}

// ---------- add, edit, delete ----------

async function addStudent(event) {
  event.preventDefault();
  const form = event.target;
  const body = {
    studentId: form.studentId.value.trim(),
    studentName: form.studentName.value.trim(),
    email: form.email.value.trim(),
    courseName: form.courseName.value.trim(),
    programmingScore: Number(form.programmingScore.value),
    mathematicsScore: Number(form.mathematicsScore.value),
    technicalSkills: parseSkills(form.technicalSkills.value)
  };
  try {
    await callApi(STUDENTS_URL, jsonOptions("POST", body));
    toast("Student " + body.studentId + " added.");
    form.reset();
    showTab("all");
  } catch (e) {
    toast(e.message, "error");
  }
}

async function openEdit(id) {
  try {
    const s = await callApi(STUDENTS_URL + "/" + encodeURIComponent(id));
    const form = $("editForm");
    $("editId").textContent = s.studentId + " (" + s.studentName + ")";
    form.dataset.id = s.studentId;
    form.programmingScore.value = s.programmingScore;
    form.mathematicsScore.value = s.mathematicsScore;
    form.technicalSkills.value = s.technicalSkills.join(", ");
    $("editDialog").showModal();
  } catch (e) {
    toast(e.message, "error");
  }
}

async function saveEdit(event) {
  event.preventDefault();
  const form = event.target;
  const body = {
    programmingScore: Number(form.programmingScore.value),
    mathematicsScore: Number(form.mathematicsScore.value),
    technicalSkills: parseSkills(form.technicalSkills.value)
  };
  try {
    await callApi(STUDENTS_URL + "/" + encodeURIComponent(form.dataset.id), jsonOptions("PUT", body));
    $("editDialog").close();
    toast("Student updated.");
    refreshCurrentTab();
  } catch (e) {
    toast(e.message, "error");
  }
}

async function deleteStudent(id) {
  if (!confirm("Delete student " + id + "?")) {
    return;
  }
  try {
    await callApi(STUDENTS_URL + "/" + encodeURIComponent(id), { method: "DELETE" });
    toast("Student " + id + " deleted.");
    refreshCurrentTab();
  } catch (e) {
    toast(e.message, "error");
  }
}

// ---------- start-up ----------

async function checkBackend() {
  const status = $("status");
  try {
    await callApi(API_BASE + "/api/docs");
    status.textContent = "Backend connected (port 8080)";
    status.className = "status ok";
  } catch (e) {
    status.textContent = "Backend offline";
    status.className = "status bad";
  }
}

document.querySelectorAll("#tabs button").forEach((b) => b.addEventListener("click", () => showTab(b.dataset.tab)));
$("searchBtn").addEventListener("click", searchStudents);
$("searchValue").addEventListener("keydown", (e) => { if (e.key === "Enter") searchStudents(); });
$("resetBtn").addEventListener("click", () => { $("searchValue").value = ""; refreshCurrentTab(); });
$("addForm").addEventListener("submit", addStudent);
$("editForm").addEventListener("submit", saveEdit);
$("cancelEdit").addEventListener("click", () => $("editDialog").close());

// Click a column heading to sort the table by it; click again to reverse.
document.querySelectorAll("th.sortable").forEach((th) => th.addEventListener("click", () => {
  sortDir = sortKey === th.dataset.sort ? -sortDir : 1;
  sortKey = th.dataset.sort;
  renderTable();
}));

// One listener for all Edit/Delete buttons in the table.
$("studentRows").addEventListener("click", (e) => {
  if (e.target.dataset.edit) openEdit(e.target.dataset.edit);
  if (e.target.dataset.delete) deleteStudent(e.target.dataset.delete);
});

checkBackend();
showTab("all");
