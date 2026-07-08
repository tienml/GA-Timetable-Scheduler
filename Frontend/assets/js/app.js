/*
  GA Timetable Scheduler - Vanilla Frontend
  Tech: HTML + CSS + Bootstrap + JavaScript
  API base URL uses ngrok instead of localhost.

  IMPORTANT:
  - GET requests are kept as simple as possible to reduce preflight issues.
  - POST/PUT/DELETE still require backend CORS to allow OPTIONS.
*/

const API_BASE_URL = 'https://grandpa-discharge-isolated.ngrok-free.dev';
const USE_NGROK_HEADER_FOR_GET = true; // Bật để ngrok không trả trang cảnh báo và terminal ngrok có log request

const app = document.getElementById('app');
const headerTitle = document.getElementById('headerTitle');
const sidebar = document.getElementById('sidebar');
const nav = document.getElementById('sidebarNav');

let currentPage = 'dashboard';
let charts = {};
let modalState = { resourceKey: null, mode: 'create', id: null };
let apiStatus = [];
let tableState = {};
const PAGE_SIZE = 5;

const PAGE_TITLES = {
  dashboard: 'Tổng quan',
  teachers: 'Quản lý giáo viên',
  subjects: 'Quản lý môn học',
  studentGroups: 'Quản lý nhóm sinh viên',
  rooms: 'Quản lý phòng học',
  equipment: 'Quản lý thiết bị',
  timeSlots: 'Quản lý khung giờ',
  courseClasses: 'Quản lý lớp học phần',
  relationships: 'Quản lý khung giờ & ràng buộc',
  generate: 'Tạo thời khóa biểu',
  results: 'Kết quả tạo thời khóa biểu'
};

const resourceConfigs = {
  teachers: {
    title: 'Quản lý giáo viên',
    subtitle: 'Quản lý thông tin giáo viên phục vụ lập thời khóa biểu.',
    endpoint: '/api/teachers',
    addText: 'Thêm giáo viên',
    importText: 'Nhập dữ liệu',
    idField: 'id',
    searchPlaceholder: 'Tìm kiếm theo mã GV, họ tên, email, SĐT...',
    fields: [
      { name: 'teacherCode', label: 'Mã giáo viên', required: true },
      { name: 'fullName', label: 'Họ tên', required: true },
      { name: 'email', label: 'Email', type: 'email' },
      { name: 'phone', label: 'Số điện thoại' }
    ],
    stats: rows => [
      statData('Tổng giáo viên', 'bi-people', 'bg-purple', rows.length, 'bản ghi'),
      statData('Có email', 'bi-envelope-check', 'bg-green', rows.filter(x => x.email).length, 'giáo viên'),
      statData('Có số điện thoại', 'bi-telephone', 'bg-blue', rows.filter(x => x.phone).length, 'giáo viên'),
      statData('Mới thêm 7 ngày', 'bi-calendar-plus', 'bg-orange', countRecent(rows), 'bản ghi')
    ],
    columns: [
      col('Mã GV', r => r.teacherCode),
      col('Họ tên', r => r.fullName),
      col('Email', r => r.email || '-'),
      col('SĐT', r => r.phone || '-'),
      col('Ngày tạo', r => formatDate(r.createdAt))
    ]
  },
  subjects: {
    title: 'Quản lý môn học',
    subtitle: 'Quản lý môn học và số tiết mặc định để thuật toán lập lịch sử dụng.',
    endpoint: '/api/subjects',
    addText: 'Thêm môn học',
    importText: 'Nhập dữ liệu',
    idField: 'id',
    searchPlaceholder: 'Tìm kiếm theo mã môn, tên môn học...',
    fields: [
      { name: 'subjectCode', label: 'Mã môn học', required: true },
      { name: 'subjectName', label: 'Tên môn học', required: true },
      { name: 'defaultPeriods', label: 'Số tiết mặc định', type: 'number', required: true, value: 2 }
    ],
    stats: rows => [
      statData('Tổng môn học', 'bi-book', 'bg-purple', rows.length, 'bản ghi'),
      statData('Tổng số tiết', 'bi-clock-history', 'bg-green', rows.reduce((s, r) => s + n(r.defaultPeriods), 0), 'tiết'),
      statData('TB tiết/môn', 'bi-bar-chart', 'bg-blue', rows.length ? (rows.reduce((s, r) => s + n(r.defaultPeriods), 0) / rows.length).toFixed(1) : 0, 'tiết'),
      statData('Mới thêm 7 ngày', 'bi-calendar-plus', 'bg-orange', countRecent(rows), 'bản ghi')
    ],
    columns: [
      col('Mã môn', r => r.subjectCode),
      col('Tên môn học', r => r.subjectName),
      col('Số tiết mặc định', r => r.defaultPeriods),
      col('Ngày tạo', r => formatDate(r.createdAt))
    ]
  },
  studentGroups: {
    title: 'Quản lý nhóm sinh viên',
    subtitle: 'Quản lý nhóm sinh viên và sĩ số để kiểm tra trùng lịch, sức chứa phòng.',
    endpoint: '/api/student-groups',
    addText: 'Thêm nhóm',
    importText: 'Nhập dữ liệu',
    idField: 'id',
    searchPlaceholder: 'Tìm kiếm theo mã nhóm, tên nhóm...',
    fields: [
      { name: 'groupCode', label: 'Mã nhóm sinh viên', required: true },
      { name: 'groupName', label: 'Tên nhóm sinh viên', required: true },
      { name: 'numberOfStudents', label: 'Số lượng sinh viên', type: 'number', required: true }
    ],
    stats: rows => [
      statData('Tổng nhóm', 'bi-people', 'bg-purple', rows.length, 'nhóm'),
      statData('Tổng sinh viên', 'bi-person', 'bg-green', rows.reduce((s, r) => s + n(r.numberOfStudents), 0), 'sinh viên'),
      statData('Sĩ số TB', 'bi-calculator', 'bg-blue', rows.length ? Math.round(rows.reduce((s, r) => s + n(r.numberOfStudents), 0) / rows.length) : 0, 'SV/nhóm'),
      statData('Mới thêm 7 ngày', 'bi-calendar-plus', 'bg-orange', countRecent(rows), 'bản ghi')
    ],
    columns: [
      col('Mã nhóm', r => r.groupCode),
      col('Tên nhóm', r => r.groupName),
      col('Số lượng SV', r => r.numberOfStudents),
      col('Ngày tạo', r => formatDate(r.createdAt))
    ]
  },
  rooms: {
    title: 'Quản lý phòng học',
    subtitle: 'Quản lý phòng học, sức chứa và vị trí phòng để lập lịch.',
    endpoint: '/api/rooms',
    addText: 'Thêm phòng học',
    importText: 'Nhập dữ liệu',
    idField: 'id',
    searchPlaceholder: 'Tìm kiếm mã phòng, tên phòng, tòa nhà...',
    fields: [
      { name: 'roomCode', label: 'Mã phòng học', required: true },
      { name: 'roomName', label: 'Tên phòng học', required: true },
      { name: 'capacity', label: 'Sức chứa', type: 'number', required: true },
      { name: 'building', label: 'Tòa nhà' },
      { name: 'floorNumber', label: 'Tầng', type: 'number' }
    ],
    stats: rows => [
      statData('Tổng phòng học', 'bi-building', 'bg-blue', rows.length, 'phòng'),
      statData('Tổng sức chứa', 'bi-person-check', 'bg-green', rows.reduce((s, r) => s + n(r.capacity), 0), 'chỗ ngồi'),
      statData('Sức chứa TB', 'bi-calculator', 'bg-orange', rows.length ? Math.round(rows.reduce((s, r) => s + n(r.capacity), 0) / rows.length) : 0, 'SV/phòng'),
      statData('Số tòa nhà', 'bi-buildings', 'bg-purple', new Set(rows.map(r => r.building).filter(Boolean)).size, 'tòa')
    ],
    columns: [
      col('Mã phòng', r => r.roomCode),
      col('Tên phòng', r => r.roomName),
      col('Tòa nhà', r => r.building || '-'),
      col('Tầng', r => r.floorNumber ?? '-'),
      col('Sức chứa', r => r.capacity),
      col('Ngày tạo', r => formatDate(r.createdAt))
    ]
  },
  equipment: {
    title: 'Quản lý thiết bị',
    subtitle: 'Quản lý thiết bị để đối chiếu với yêu cầu thiết bị của lớp học phần.',
    endpoint: '/api/equipment',
    addText: 'Thêm thiết bị',
    importText: 'Nhập dữ liệu',
    idField: 'id',
    searchPlaceholder: 'Tìm kiếm theo mã thiết bị, tên thiết bị...',
    fields: [
      { name: 'equipmentCode', label: 'Mã thiết bị', required: true },
      { name: 'equipmentName', label: 'Tên thiết bị', required: true }
    ],
    stats: rows => [
      statData('Tổng thiết bị', 'bi-display', 'bg-blue', rows.length, 'bản ghi'),
      statData('Máy chiếu', 'bi-projector', 'bg-green', rows.filter(x => text(x).includes('chiếu')).length, 'bản ghi'),
      statData('Máy tính/Lab', 'bi-pc-display', 'bg-purple', rows.filter(x => text(x).includes('máy') || text(x).includes('lab')).length, 'bản ghi'),
      statData('Mới thêm 7 ngày', 'bi-calendar-plus', 'bg-orange', countRecent(rows), 'bản ghi')
    ],
    columns: [
      col('Mã thiết bị', r => r.equipmentCode),
      col('Tên thiết bị', r => r.equipmentName)
    ]
  },
  timeSlots: {
    title: 'Quản lý khung giờ',
    subtitle: 'Quản lý thứ, ca học và thời gian bắt đầu/kết thúc.',
    endpoint: '/api/timeslots',
    addText: 'Thêm khung giờ',
    importText: 'Thiết lập ca học',
    idField: 'id',
    searchPlaceholder: 'Tìm kiếm theo thứ, ca học, thời gian...',
    fields: [
      { name: 'dayOfWeek', label: 'Thứ trong tuần', type: 'number', required: true, value: 2 },
      { name: 'slotIndex', label: 'Ca học', type: 'number', required: true, value: 1 },
      { name: 'startTime', label: 'Giờ bắt đầu', type: 'time', required: true },
      { name: 'endTime', label: 'Giờ kết thúc', type: 'time', required: true },
      { name: 'description', label: 'Mô tả' }
    ],
    stats: rows => [
      statData('Tổng khung giờ', 'bi-calendar2-week', 'bg-blue', rows.length, 'khung giờ'),
      statData('Số ngày có lịch', 'bi-calendar3', 'bg-green', new Set(rows.map(x => x.dayOfWeek)).size, 'ngày'),
      statData('Số ca lớn nhất', 'bi-clock', 'bg-orange', rows.reduce((m, x) => Math.max(m, n(x.slotIndex)), 0), 'ca/ngày'),
      statData('Khung giờ mới', 'bi-calendar-plus', 'bg-purple', countRecent(rows), 'bản ghi')
    ],
    columns: [
      col('Thứ', r => r.dayName || dayName(r.dayOfWeek)),
      col('Ca học', r => `Ca ${r.slotIndex}`),
      col('Bắt đầu', r => formatTime(r.startTime)),
      col('Kết thúc', r => formatTime(r.endTime)),
      col('Mô tả', r => r.description || '-')
    ]
  },
  courseClasses: {
    title: 'Quản lý lớp học phần',
    subtitle: 'Quản lý lớp học phần, giáo viên, nhóm sinh viên, số tiết và sĩ số.',
    endpoint: '/api/course-classes',
    addText: 'Thêm lớp học phần',
    importText: 'Nhập dữ liệu',
    idField: 'id',
    searchPlaceholder: 'Tìm kiếm mã lớp, môn học, giáo viên...',
    fields: [
      { name: 'classCode', label: 'Mã lớp học phần', required: true },
      { name: 'subjectId', label: 'ID môn học', type: 'number', required: true },
      { name: 'teacherId', label: 'ID giáo viên', type: 'number', required: true },
      { name: 'studentGroupId', label: 'ID nhóm sinh viên', type: 'number', required: true },
      { name: 'numberOfStudents', label: 'Số lượng sinh viên', type: 'number', required: true },
      { name: 'periods', label: 'Số tiết', type: 'number', required: true, value: 2 },
      { name: 'note', label: 'Ghi chú' }
    ],
    stats: rows => [
      statData('Tổng lớp học phần', 'bi-mortarboard', 'bg-blue', rows.length, 'lớp'),
      statData('Tổng sinh viên học', 'bi-people', 'bg-green', rows.reduce((s, r) => s + n(r.numberOfStudents), 0), 'lượt SV'),
      statData('Tổng số tiết', 'bi-clock-history', 'bg-orange', rows.reduce((s, r) => s + n(r.periods), 0), 'tiết'),
      statData('Mới thêm 7 ngày', 'bi-calendar-plus', 'bg-purple', countRecent(rows), 'bản ghi')
    ],
    columns: [
      col('Mã lớp', r => r.classCode),
      col('Môn học', r => r.subjectName || r.subjectCode || r.subject?.subjectName || '-'),
      col('Giáo viên', r => r.teacherName || r.teacher?.fullName || '-'),
      col('Nhóm SV', r => r.studentGroupName || r.studentGroup?.groupName || '-'),
      col('Số lượng SV', r => r.numberOfStudents),
      col('Số tiết', r => r.periods),
      col('Ghi chú', r => r.note || '-')
    ]
  }
};

const relationshipEndpoints = {
  teacherSubjects: '/api/teacher-subjects',
  teacherAvailableTimes: '/api/teacher-available-times',
  teacherPreferredTimes: '/api/teacher-preferred-times',
  roomAvailableTimes: '/api/room-available-times',
  roomEquipment: '/api/room-equipment',
  courseClassRequiredEquipment: '/api/course-class-required-equipment'
};

function col(label, value) { return { label, value }; }
function statData(label, icon, color, value, sub) { return { label, icon, color, value, sub }; }
function n(v) { return Number(v) || 0; }
function text(obj) { return Object.values(obj || {}).join(' ').toLowerCase(); }

function setActive(page) {
  currentPage = page;
  headerTitle.textContent = PAGE_TITLES[page] || 'GA Timetable Scheduler';
  document.querySelectorAll('.nav-item').forEach(btn => btn.classList.toggle('active', btn.dataset.page === page));
}

function renderLoader() {
  app.innerHTML = `<div class="loader"><div class="spinner-border text-primary" role="status"></div></div>`;
}

function buildHeaders(method, body) {
  const headers = { Accept: 'application/json' };
  if (body) headers['Content-Type'] = 'application/json';

  // Custom header causes browser preflight. Use it only when needed.
  if (method !== 'GET' || USE_NGROK_HEADER_FOR_GET) {
    headers['ngrok-skip-browser-warning'] = 'true';
  }
  return headers;
}

async function apiRequest(endpoint, options = {}) {
  const method = (options.method || 'GET').toUpperCase();
  console.info(`[API] ${method} ${API_BASE_URL}${endpoint}`);
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    method,
    cache: 'no-store',
    headers: {
      ...buildHeaders(method, options.body),
      ...(options.headers || {})
    }
  });

  const text = await response.text();
  let json = null;
  try { json = text ? JSON.parse(text) : null; } catch { json = text; }

  console.info(`[API] ${method} ${endpoint} -> ${response.status}`);

  if (!response.ok) {
    const message = json?.message || json?.error || `API lỗi ${response.status}`;
    throw new Error(message);
  }

  if (typeof json === 'string' && json.toLowerCase().includes('ngrok')) {
    throw new Error('Ngrok đang trả trang cảnh báo thay vì JSON. Hãy bật CORS backend và cho phép header ngrok-skip-browser-warning.');
  }

  return unwrapApi(json);
}

function unwrapApi(payload) {
  if (payload && typeof payload === 'object' && 'data' in payload) return payload.data;
  return payload;
}

async function fetchResource(key, endpoint = null) {
  const url = endpoint || resourceConfigs[key]?.endpoint;
  try {
    const data = await apiRequest(url);
    const rows = Array.isArray(data) ? data : (data?.content || []);
    return { rows, error: null };
  } catch (err) {
    console.warn(`[${key}] API lỗi:`, err.message);
    return { rows: [], error: err.message };
  }
}

async function fetchAllBaseData() {
  const result = await Promise.all([
    fetchResource('teachers'),
    fetchResource('subjects'),
    fetchResource('studentGroups'),
    fetchResource('rooms'),
    fetchResource('equipment'),
    fetchResource('timeSlots'),
    fetchResource('courseClasses'),
    fetchResource('scheduleRuns', '/api/timetable/runs')
  ]);

  const keys = ['teachers', 'subjects', 'studentGroups', 'rooms', 'equipment', 'timeSlots', 'courseClasses', 'scheduleRuns'];
  const data = {};
  apiStatus = [];
  keys.forEach((k, i) => {
    data[k] = result[i].rows;
    if (result[i].error) apiStatus.push(`${k}: ${result[i].error}`);
  });
  return data;
}

function apiWarningHtml() {
  return '';
}

function pageHeading(title, subtitle, actions = '') {
  return `
    <div class="page-heading">
      <div>
        <h1>${title}</h1>
        <p>${subtitle || ''}</p>
      </div>
      <div class="d-flex gap-2 flex-wrap">${actions}</div>
    </div>
  `;
}

function statCard({ label, icon, color, value, sub }) {
  return `
    <div class="stat-card">
      <div class="stat-icon ${color}"><i class="bi ${icon}"></i></div>
      <div>
        <div class="stat-title">${label}</div>
        <div class="stat-value">${safeText(value)}</div>
        <div class="stat-sub">${sub || ''}</div>
      </div>
    </div>
  `;
}

function renderStats(stats) {
  return `<div class="stat-grid">${stats.map(statCard).join('')}</div>`;
}

function statusBadge(text, type = 'green') {
  return `<span class="status-pill status-${type}">${text}</span>`;
}

function tag(text, type = 'blue') {
  return `<span class="tag-pill tag-${type} me-1">${text}</span>`;
}

function dayName(n) {
  return { 2: 'Thứ 2', 3: 'Thứ 3', 4: 'Thứ 4', 5: 'Thứ 5', 6: 'Thứ 6', 7: 'Thứ 7', 8: 'Chủ nhật' }[Number(n)] || `Thứ ${n}`;
}

function safeText(value) {
  return value === null || value === undefined || value === '' ? '-' : String(value);
}

function formatDate(value) {
  if (!value) return '-';
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return String(value);
  return d.toLocaleString('vi-VN', { hour12: false });
}

function formatTime(value) {
  if (!value) return '-';
  return String(value).slice(0, 5);
}

function countRecent(rows) {
  const now = Date.now();
  const seven = 7 * 24 * 60 * 60 * 1000;
  return rows.filter(r => {
    const d = new Date(r.createdAt || r.createdDate || r.created_at || 0);
    return !Number.isNaN(d.getTime()) && now - d.getTime() <= seven;
  }).length;
}

function renderFilter(config, key) {
  return `
    <div class="filter-card mb-3">
      <div class="row g-3 align-items-end">
        <div class="col-lg-7">
          <label class="form-label small text-muted">Tìm kiếm</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
            <input id="searchInput" type="text" class="form-control" placeholder="${config.searchPlaceholder || 'Tìm kiếm...'}">
          </div>
        </div>
        <div class="col-lg-3 col-md-6">
          <label class="form-label small text-muted">Sắp xếp</label>
          <select id="sortSelect" class="form-select">
            <option value="default">Mặc định</option>
            <option value="az">Tên / mã A-Z</option>
            <option value="za">Tên / mã Z-A</option>
            <option value="newest">Mới nhất</option>
            <option value="oldest">Cũ nhất</option>
          </select>
        </div>
        <div class="col-lg-2 col-md-6 d-grid">
          <label class="form-label small text-muted">Thao tác</label>
          <button id="resetFilterBtn" class="btn btn-light border" type="button"><i class="bi bi-arrow-counterclockwise me-1"></i>Đặt lại</button>
        </div>
      </div>
    </div>
  `;
}

function getRowSortText(row) {
  return String(
    row.teacherCode || row.fullName || row.subjectCode || row.subjectName || row.groupCode || row.groupName ||
    row.roomCode || row.roomName || row.equipmentCode || row.equipmentName || row.classCode || row.dayName || row.id || ''
  ).toLowerCase();
}

function sortRows(rows, sortMode) {
  const cloned = [...rows];
  if (sortMode === 'az') return cloned.sort((a, b) => getRowSortText(a).localeCompare(getRowSortText(b), 'vi'));
  if (sortMode === 'za') return cloned.sort((a, b) => getRowSortText(b).localeCompare(getRowSortText(a), 'vi'));
  if (sortMode === 'newest') return cloned.sort((a, b) => new Date(b.createdAt || b.createdDate || b.created_at || 0) - new Date(a.createdAt || a.createdDate || a.created_at || 0));
  if (sortMode === 'oldest') return cloned.sort((a, b) => new Date(a.createdAt || a.createdDate || a.created_at || 0) - new Date(b.createdAt || b.createdDate || b.created_at || 0));
  return cloned;
}

function getFilteredRows(key) {
  const state = tableState[key] || { rows: [], search: '', sort: 'default', page: 1 };
  const q = (state.search || '').toLowerCase().trim();
  const filtered = state.rows.filter(row => !q || text(row).includes(q));
  return sortRows(filtered, state.sort);
}

function renderTable(key, rows) {
  const config = resourceConfigs[key];
  const columns = config.columns || [];
  const state = tableState[key] || { rows, search: '', sort: 'default', page: 1 };
  const filtered = getFilteredRows(key);
  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  state.page = Math.min(Math.max(1, state.page || 1), totalPages);
  tableState[key] = state;
  const startIndex = (state.page - 1) * PAGE_SIZE;
  const visibleRows = filtered.slice(startIndex, startIndex + PAGE_SIZE);
  const bodyRows = visibleRows.map(row => `
    <tr>
      ${columns.map(col => `<td>${safeText(col.value(row))}</td>`).join('')}
      <td class="text-nowrap">
        <button class="action-btn" title="Xem" onclick='viewRow(${escapeJson(row)})'><i class="bi bi-eye"></i></button>
        <button class="action-btn" title="Sửa" onclick="openForm('${key}', 'edit', ${row[config.idField]})"><i class="bi bi-pencil"></i></button>
        <button class="action-btn danger" title="Xóa" onclick="deleteEntity('${key}', ${row[config.idField]})"><i class="bi bi-trash"></i></button>
      </td>
    </tr>
  `).join('');

  return `
    <div class="table-card">
      <div class="table-responsive">
        <table class="table align-middle" id="dataTable">
          <thead>
            <tr>
              ${columns.map(col => `<th>${col.label} <i class="bi bi-arrow-down-up small text-muted"></i></th>`).join('')}
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>${bodyRows || `<tr><td colspan="${columns.length + 1}"><div class="empty-state">Không có dữ liệu phù hợp</div></td></tr>`}</tbody>
        </table>
      </div>
      <div class="table-footer">
        <div class="text-muted small">Hiển thị <strong>${visibleRows.length}</strong> / ${filtered.length} kết quả</div>
        <div class="btn-group btn-group-sm">
          <button class="btn btn-light border" onclick="changeTablePage('${key}', ${state.page - 1})" ${state.page <= 1 ? 'disabled' : ''}><i class="bi bi-chevron-left"></i></button>
          ${Array.from({ length: totalPages }, (_, i) => i + 1).slice(Math.max(0, state.page - 3), Math.max(5, state.page + 2)).map(p => `<button class="btn ${p === state.page ? 'btn-primary' : 'btn-light border'}" onclick="changeTablePage('${key}', ${p})">${p}</button>`).join('')}
          <button class="btn btn-light border" onclick="changeTablePage('${key}', ${state.page + 1})" ${state.page >= totalPages ? 'disabled' : ''}><i class="bi bi-chevron-right"></i></button>
        </div>
      </div>
    </div>
  `;
}

function refreshResourceTable(key) {
  const holder = document.getElementById('tableHost');
  if (holder) holder.innerHTML = renderTable(key, tableState[key]?.rows || []);
}

function changeTablePage(key, page) {
  if (!tableState[key]) return;
  tableState[key].page = page;
  refreshResourceTable(key);
}

function escapeJson(obj) {
  return JSON.stringify(obj).replace(/'/g, '&apos;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

async function renderResourcePage(key) {
  setActive(key);
  renderLoader();
  const config = resourceConfigs[key];
  const result = await fetchResource(key);
  apiStatus = result.error ? [`${key}: ${result.error}`] : [];
  const rows = result.rows;
  tableState[key] = { rows, search: '', sort: 'default', page: 1 };

  const actions = `
    <button class="btn btn-primary" onclick="openForm('${key}', 'create')"><i class="bi bi-plus-lg me-1"></i>${config.addText}</button>
  `;

  app.innerHTML = `
    ${pageHeading(config.title, config.subtitle, actions)}
    ${renderStats(config.stats(rows))}
    ${renderFilter(config, key)}
    <div id="tableHost">${renderTable(key, rows)}</div>
    <footer class="text-center text-muted small mt-4">© 2025 GA Timetable Scheduler. All rights reserved.</footer>
  `;
  setupSearchFilter(key);
}

function setupSearchFilter(key) {
  const input = document.getElementById('searchInput');
  const sort = document.getElementById('sortSelect');
  const reset = document.getElementById('resetFilterBtn');
  if (!tableState[key]) return;
  if (input) input.addEventListener('input', () => {
    tableState[key].search = input.value;
    tableState[key].page = 1;
    refreshResourceTable(key);
  });
  if (sort) sort.addEventListener('change', () => {
    tableState[key].sort = sort.value;
    tableState[key].page = 1;
    refreshResourceTable(key);
  });
  if (reset) reset.addEventListener('click', () => {
    tableState[key].search = '';
    tableState[key].sort = 'default';
    tableState[key].page = 1;
    if (input) input.value = '';
    if (sort) sort.value = 'default';
    refreshResourceTable(key);
  });
}

async function renderDashboard() {
  setActive('dashboard');
  renderLoader();
  const data = await fetchAllBaseData();
  const latest = await fetchLatestTimetable(false);
  const latestEntries = latest.entries || [];

  const roomUsage = calcRoomUsage(data.rooms, latestEntries);
  const recentRuns = sortByDate(data.scheduleRuns, 'createdAt').slice(0, 5);
  const latestRun = recentRuns[0] || latest.scheduleRun || null;

  app.innerHTML = `
    ${pageHeading('Tổng quan hệ thống', 'Hệ thống tự động lập thời khóa biểu thông minh sử dụng thuật toán di truyền (Genetic Algorithm).', `
      <div class="date-chip"><i class="bi bi-calendar3 me-2"></i>${new Date().toLocaleDateString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' })}</div>
    `)}
    ${renderStats([
      statData('Tổng giáo viên', 'bi-people', 'bg-purple', data.teachers.length, 'giáo viên'),
      statData('Tổng phòng học', 'bi-building', 'bg-green', data.rooms.length, 'phòng'),
      statData('Tổng lớp học phần', 'bi-mortarboard', 'bg-blue', data.courseClasses.length, 'lớp'),
      statData('Lần tạo lịch gần nhất', 'bi-clock-history', 'bg-orange', latestRun ? formatDate(latestRun.createdAt) : '-', latestRun ? `bởi ${latestRun.createdBy || 'admin'}` : 'chưa có phiên')
    ])}

    <div class="dashboard-charts-grid mb-3">
      <div class="card-panel chart-card-old">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <div class="card-title mb-0">Hoạt động tạo lịch theo tuần <i class="bi bi-info-circle text-muted small ms-1" title="Dữ liệu lấy từ API /api/timetable/runs"></i></div>
          <select class="form-select form-select-sm chart-select" disabled><option>7 ngày qua</option></select>
        </div>
        <div class="fixed-chart fixed-chart-weekly"><canvas id="weeklyChart"></canvas></div>
        <div class="weekly-summary mt-3">
          <div><small class="text-muted">Tổng số lần tạo lịch</small><div class="weekly-total">${data.scheduleRuns.length}</div></div>
          <span class="stat-up"><i class="bi bi-arrow-up"></i>${data.scheduleRuns.length ? 'Dữ liệu thật' : '0 phiên'}</span>
        </div>
      </div>

      <div class="card-panel chart-card-old">
        <div class="card-title">Tình trạng sử dụng phòng học <i class="bi bi-info-circle text-muted small ms-1" title="Tính theo /api/rooms và /api/timetable/latest"></i></div>
        <div class="room-usage-layout">
          <div class="room-donut-wrap fixed-chart-donut"><canvas id="roomChart"></canvas></div>
          <div class="kv-list room-legend">
            <div class="kv-row"><span><span class="dot dot-blue"></span>Đang sử dụng</span><b>${roomUsage.used}</b></div>
            <div class="kv-row"><span><span class="dot dot-green"></span>Trống</span><b>${roomUsage.free}</b></div>
            <div class="kv-row"><span><span class="dot dot-orange"></span>Bảo trì</span><b>${roomUsage.maintenance}</b></div>
          </div>
        </div>
        <a class="small fw-bold text-decoration-none" href="javascript:void(0)" onclick="goTo('rooms')">Xem chi tiết phòng học <i class="bi bi-chevron-right"></i></a>
      </div>

      <div class="card-panel quick-panel clean-quick-panel">
        <div class="card-title">Thao tác nhanh</div>
        <div class="quick-action-grid">
          ${quickButton('bi-plus', 'Thêm dữ liệu', 'Giáo viên, môn, phòng...', 'bg-blue', 'teachers')}
          ${quickButton('bi-calendar2-plus', 'Tạo lịch', 'Chạy thuật toán GA', 'bg-green', 'generate')}
          ${quickButton('bi-bar-chart', 'Xem kết quả', 'Lịch đã tạo', 'bg-orange', 'results')}
        </div>
      </div>
    </div>

    <div class="card-panel">
      <div class="card-title">Các lần tạo lịch gần đây</div>
      <div class="table-responsive">
        <table class="table align-middle mb-0">
          <thead><tr><th>Thời gian</th><th>Tên lần chạy</th><th>Penalty</th><th>Ràng buộc cứng</th><th>Fitness ước tính</th><th>Trạng thái</th></tr></thead>
          <tbody>
            ${recentRuns.map(r => `<tr><td>${formatDate(r.createdAt)}</td><td>${safeText(r.runName)}</td><td>${safeText(r.totalPenalty)}</td><td>${safeText(r.hardConstraintViolations)}</td><td>${calcFitness(r)}</td><td>${statusBadge(r.status || 'COMPLETED', (r.status || '').toLowerCase().includes('fail') ? 'red' : 'green')}</td></tr>`).join('') || `<tr><td colspan="6"><div class="empty-state">Chưa có dữ liệu phiên tạo lịch</div></td></tr>`}
          </tbody>
        </table>
      </div>
    </div>
    <footer class="text-center text-muted small mt-4">© 2025 GA Timetable Scheduler. All rights reserved.</footer>
  `;

  renderDashboardCharts(data.scheduleRuns, roomUsage);
}

function quickButton(icon, title, sub, color, page) {
  return `
    <button class="quick-item" onclick="goTo('${page}')">
      <span class="left"><span class="quick-icon ${color}"><i class="bi ${icon}"></i></span><span><b>${title}</b><br><small>${sub}</small></span></span>
      <i class="bi bi-chevron-right"></i>
    </button>`;
}

function calcRoomUsage(rooms, entries) {
  const usedIds = new Set(entries.map(e => e.roomId).filter(Boolean));
  const usedNames = new Set(entries.map(e => e.roomName || e.roomCode).filter(Boolean));
  const used = usedIds.size || usedNames.size;
  const maintenance = rooms.filter(r => text(r).includes('bảo trì') || text(r).includes('bao tri')).length;
  return { used, maintenance, free: Math.max(0, rooms.length - used - maintenance) };
}

function sortByDate(rows, field) {
  return [...(rows || [])].sort((a, b) => new Date(b[field] || 0) - new Date(a[field] || 0));
}

function renderDashboardCharts(scheduleRuns, roomUsage) {
  destroyCharts();
  if (!window.Chart) return;

  const weeklyData = groupRunsLast7Days(scheduleRuns);
  const weekly = document.getElementById('weeklyChart');
  if (weekly) {
    charts.weekly = new Chart(weekly, {
      type: 'line',
      data: {
        labels: weeklyData.labels,
        datasets: [{
          label:'Số lần tạo lịch',
          data: weeklyData.values,
          tension:.38,
          fill:true,
          borderWidth: 3,
          borderColor: '#3aa0e6',
          backgroundColor: 'rgba(58, 160, 230, .42)',
          pointBackgroundColor: '#3aa0e6',
          pointBorderColor: '#3aa0e6',
          pointRadius: 4
        }]
      },
      options: {
        responsive:true,
        maintainAspectRatio:false,
        animation:false,
        normalized:true,
        plugins:{ legend:{ display:false } },
        scales:{
          y:{ beginAtZero:true, suggestedMax: Math.max(35, ...weeklyData.values), ticks:{ precision:0 }, grid:{ color:'rgba(15,23,42,.10)' } },
          x:{ grid:{ color:'rgba(15,23,42,.08)' } }
        }
      }
    });
  }

  const room = document.getElementById('roomChart');
  if (room) {
    charts.room = new Chart(room, {
      type:'doughnut',
      data:{
        labels:['Đang sử dụng','Trống','Bảo trì'],
        datasets:[{
          data:(roomUsage.used + roomUsage.free + roomUsage.maintenance) ? [roomUsage.used, roomUsage.free, roomUsage.maintenance] : [1],
          backgroundColor:(roomUsage.used + roomUsage.free + roomUsage.maintenance) ? ['#36a2eb','#12b76a','#ff8a00'] : ['#edf0f5'],
          borderWidth: 0
        }]
      },
      options:{ responsive:true, maintainAspectRatio:false, plugins:{ legend:{ display:false } }, cutout:'66%' }
    });
  }
}

function groupRunsLast7Days(runs) {
  const labels = [];
  const values = [];
  const map = new Map();
  const today = new Date();
  for (let i = 6; i >= 0; i--) {
    const d = new Date(today);
    d.setDate(today.getDate() - i);
    const key = d.toISOString().slice(0, 10);
    labels.push(d.toLocaleDateString('vi-VN', { weekday: 'short' }));
    map.set(key, 0);
  }
  (runs || []).forEach(r => {
    const d = new Date(r.createdAt || r.created_at || 0);
    if (!Number.isNaN(d.getTime())) {
      const key = d.toISOString().slice(0, 10);
      if (map.has(key)) map.set(key, map.get(key) + 1);
    }
  });
  map.forEach(v => values.push(v));
  return { labels, values };
}

async function renderRelationships() {
  setActive('relationships');
  renderLoader();

  const timeSlotsResult = await fetchResource('timeSlots');
  const relResults = await Promise.all(Object.entries(relationshipEndpoints).map(async ([key, endpoint]) => [key, await fetchResource(key, endpoint)]));
  const rel = Object.fromEntries(relResults.map(([key, res]) => [key, res.rows]));
  apiStatus = [];
  if (timeSlotsResult.error) apiStatus.push(`timeslots: ${timeSlotsResult.error}`);
  relResults.forEach(([key, res]) => { if (res.error) apiStatus.push(`${key}: ${res.error}`); });

  const timeSlots = timeSlotsResult.rows;

  app.innerHTML = `
    ${pageHeading('Quản lý khung giờ và ràng buộc', 'Khung giờ lấy từ API; ràng buộc thể hiện các luật thuật toán GA đang áp dụng.', `
      <button class="btn btn-primary" onclick="openForm('timeSlots','create')"><i class="bi bi-plus-lg me-1"></i>Thêm khung giờ</button>
    `)}
    ${renderStats([
      statData('Tổng khung giờ', 'bi-calendar2-week', 'bg-blue', timeSlots.length, 'khung giờ'),
      statData('GV - Môn học', 'bi-person-check', 'bg-green', rel.teacherSubjects.length, 'ràng buộc'),
      statData('Phòng - Thiết bị', 'bi-pc-display', 'bg-purple', rel.roomEquipment.length, 'ràng buộc'),
      statData('Lớp yêu cầu TB', 'bi-diagram-3', 'bg-orange', rel.courseClassRequiredEquipment.length, 'ràng buộc')
    ])}
    <div class="row g-3">
      <div class="col-xl-6">
        <div class="card-panel">
          <div class="d-flex justify-content-between align-items-center mb-3"><div class="card-title mb-0">1. Khung giờ giảng dạy</div><button class="btn btn-sm btn-primary" onclick="openForm('timeSlots','create')">+ Thêm</button></div>
          <div class="table-responsive">
            <table class="table align-middle">
              <thead><tr><th>Thứ</th><th>Ca học</th><th>Bắt đầu</th><th>Kết thúc</th><th>Mô tả</th></tr></thead>
              <tbody>${timeSlots.slice(0, 5).map(t => `<tr><td>${t.dayName || dayName(t.dayOfWeek)}</td><td>${tag('Ca '+t.slotIndex,'blue')}</td><td>${formatTime(t.startTime)}</td><td>${formatTime(t.endTime)}</td><td>${t.description || '-'}</td></tr>`).join('') || `<tr><td colspan="5"><div class="empty-state">Chưa có khung giờ từ API</div></td></tr>`}</tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="col-xl-6">
        <div class="card-panel">
          <div class="card-title">2. Ràng buộc thuật toán</div>
          <div class="kv-list">
            ${constraintRow('Không trùng giáo viên', 'Kiểm tra teacherId + timeSlotId trong kết quả lịch.', 'Cứng', 'red')}
            ${constraintRow('Không trùng nhóm sinh viên', 'Kiểm tra studentGroupId + timeSlotId.', 'Cứng', 'red')}
            ${constraintRow('Không trùng phòng', 'Kiểm tra roomId + timeSlotId.', 'Cứng', 'red')}
            ${constraintRow('Phòng đủ sức chứa', 'So sánh capacity với numberOfStudents.', 'Cứng', 'red')}
            ${constraintRow('Phòng đủ thiết bị', `Có ${rel.courseClassRequiredEquipment.length} yêu cầu thiết bị lớp học phần.`, 'Cứng', 'red')}
            ${constraintRow('Ưu tiên thời gian giáo viên', `Có ${rel.teacherPreferredTimes.length} khung giờ ưu tiên.`, 'Mềm', 'purple')}
            ${constraintRow('Tối ưu sử dụng phòng', 'Giảm lãng phí sức chứa phòng.', 'Mềm', 'purple')}
          </div>
        </div>
      </div>
    </div>
    <footer class="text-center text-muted small mt-4">© 2025 GA Timetable Scheduler. All rights reserved.</footer>
  `;
}

function constraintRow(title, sub, type, color) {
  return `
    <div class="kv-row align-items-start">
      <div><b>${title}</b><br><small class="text-muted">${sub}</small></div>
      <div class="text-nowrap">${tag(type, color)} ${statusBadge('Đang áp dụng','green')}</div>
    </div>`;
}

async function renderGenerate() {
  setActive('generate');
  renderLoader();
  const data = await fetchAllBaseData();
  app.innerHTML = `
    ${pageHeading('Tạo thời khóa biểu', 'Cấu hình và chạy thuật toán di truyền (Genetic Algorithm) để tạo thời khóa biểu tối ưu.')}
    <div class="row g-3">
      <div class="col-xl-8">
        <div class="card-panel">
          <div class="card-title">Cấu hình thuật toán GA</div>
          <form id="generateForm" class="row g-3">
            <div class="col-12"><label class="form-label">Tên lần chạy</label><input class="form-control" name="runName" placeholder="VD: Lịch học kỳ 2025 - Thử nghiệm 1"></div>
            <div class="col-md-6"><label class="form-label">Kích thước quần thể</label><input class="form-control" name="populationSize" type="number" value="100" min="10"></div>
            <div class="col-md-6"><label class="form-label">Số thế hệ</label><input class="form-control" name="generations" type="number" value="200" min="10"></div>
            <div class="col-md-6"><label class="form-label">Tỷ lệ đột biến</label><input class="form-control" name="mutationRate" type="number" step="0.01" value="0.02" min="0" max="1"></div>
            <div class="col-md-6"><label class="form-label">Tỷ lệ lai ghép</label><input class="form-control" name="crossoverRate" type="number" step="0.01" value="0.80" min="0" max="1"></div>
            <div class="col-12">
              <label class="form-label">Ràng buộc đang áp dụng</label>
              <div class="row g-3">
                ${['Không trùng giáo viên','Không trùng nhóm sinh viên','Không trùng phòng','Phòng đủ sức chứa','Phòng đủ thiết bị','Ưu tiên thời gian giáo viên'].map((x,i)=>`<div class="col-md-4"><div class="constraint-tile"><span><i class="bi ${['bi-person','bi-people','bi-door-open','bi-person-check','bi-pc-display','bi-clock'][i]} me-2"></i>${x}</span><i class="bi bi-check-circle-fill text-success"></i></div></div>`).join('')}
              </div>
            </div>
            <div class="col-12">
              <div class="row g-3">
                <div class="col-md-3">${smallDataCard('bi-people','Giáo viên',data.teachers.length,'bản ghi','bg-purple')}</div>
                <div class="col-md-3">${smallDataCard('bi-building','Phòng học',data.rooms.length,'bản ghi','bg-green')}</div>
                <div class="col-md-3">${smallDataCard('bi-mortarboard','Lớp học phần',data.courseClasses.length,'bản ghi','bg-blue')}</div>
                <div class="col-md-3">${smallDataCard('bi-clock','Khung giờ',data.timeSlots.length,'bản ghi','bg-orange')}</div>
              </div>
            </div>
            <div class="col-12 d-flex justify-content-end">
              <button class="btn btn-primary btn-lg" type="submit"><i class="bi bi-play-fill me-1"></i>Tạo lịch ngay</button>
            </div>
          </form>
          <div id="generateAlert" class="mt-3"></div>
        </div>
      </div>
      <div class="col-xl-4">
        <div class="card-panel">
          <div class="card-title">Quy trình GA</div>
          ${gaStep(1,'Khởi tạo','Tạo ngẫu nhiên các cá thể lịch biểu ban đầu.')}
          ${gaStep(2,'Tính fitness','Đánh giá mức độ thỏa mãn ràng buộc.')}
          ${gaStep(3,'Lai ghép & đột biến','Sinh thế hệ mới và duy trì sự đa dạng.')}
          ${gaStep(4,'Chọn lịch tốt nhất','Chọn lịch có fitness cao nhất làm kết quả cuối.')}
        </div>
      </div>
    </div>
    <footer class="text-center text-muted small mt-4">© 2025 GA Timetable Scheduler. All rights reserved.</footer>
  `;
  document.getElementById('generateForm').addEventListener('submit', submitGenerate);
}

function smallDataCard(icon, label, value, sub, color) {
  return `<div class="border rounded-4 p-3 h-100"><div class="d-flex align-items-center gap-2"><span class="stat-icon ${color}" style="width:42px;height:42px;font-size:18px"><i class="bi ${icon}"></i></span><div><small class="text-muted">${label}</small><div class="fw-bold fs-4">${value}</div><small>${sub}</small></div></div></div>`;
}

function gaStep(n, title, sub) {
  return `<div class="ga-step"><div class="step-num">${n}</div><div class="step-card"><b>${title}</b><br><small class="text-muted">${sub}</small></div></div>`;
}

async function submitGenerate(e) {
  e.preventDefault();
  const form = new FormData(e.target);
  const body = {
    runName: form.get('runName') || `GA Run ${new Date().toLocaleString('vi-VN')}`,
    populationSize: Number(form.get('populationSize')),
    generations: Number(form.get('generations')),
    mutationRate: Number(form.get('mutationRate')),
    crossoverRate: Number(form.get('crossoverRate'))
  };
  const alert = document.getElementById('generateAlert');
  alert.innerHTML = `<div class="alert alert-info"><span class="spinner-border spinner-border-sm me-2"></span>Đang chạy thuật toán GA...</div>`;
  try {
    const result = await apiRequest('/api/timetable/generate', { method: 'POST', body: JSON.stringify(body) });
    localStorage.setItem('latestTimetableResult', JSON.stringify(result));
    showToast('Tạo thời khóa biểu thành công', 'success');
    goTo('results');
  } catch (err) {
    alert.innerHTML = `<div class="alert alert-danger"><b>Tạo lịch thất bại:</b> ${err.message}<br><small>Nếu lỗi 403 OPTIONS, hãy sửa CORS backend.</small></div>`;
  }
}

async function fetchLatestTimetable(updateStatus = true) {
  try {
    const result = await apiRequest('/api/timetable/latest');
    return normalizeTimetableResult(result);
  } catch (e) {
    if (updateStatus) apiStatus.push(`latest timetable: ${e.message}`);
    try {
      return normalizeTimetableResult(JSON.parse(localStorage.getItem('latestTimetableResult') || 'null'));
    } catch {
      return { scheduleRun: null, entries: [] };
    }
  }
}

function normalizeTimetableResult(result) {
  if (!result) return { scheduleRun: null, entries: [] };
  return {
    scheduleRun: result.scheduleRun || result.run || result,
    entries: result.timetableEntries || result.entries || []
  };
}

async function renderResults() {
  setActive('results');
  renderLoader();

  const latest = await fetchLatestTimetable(true);
  const runsResult = await fetchResource('scheduleRuns', '/api/timetable/runs');
  if (runsResult.error) apiStatus.push(`schedule runs: ${runsResult.error}`);
  const scheduleRun = latest.scheduleRun || {};
  const entries = latest.entries || [];
  const runs = runsResult.rows || [];

  app.innerHTML = `
    ${pageHeading('Kết quả tạo thời khóa biểu', 'Kết quả và biểu đồ được lấy từ API tạo lịch mới nhất.', `
      <button class="btn btn-light border" onclick="goTo('generate')"><i class="bi bi-arrow-clockwise me-1"></i>Tạo lại lịch</button>
      <button class="btn btn-outline-success" onclick="exportTimetableToExcel()"><i class="bi bi-file-earmark-excel me-1"></i>Xuất Excel</button>
    `)}
    ${renderStats([
      statData('Population Size', 'bi-people', 'bg-purple', scheduleRun.populationSize || '-', 'Kích thước quần thể'),
      statData('Generations', 'bi-diagram-3', 'bg-green', scheduleRun.generations || '-', 'Số thế hệ'),
      statData('Mutation Rate', 'bi-shuffle', 'bg-blue', scheduleRun.mutationRate ?? '-', 'Tỷ lệ đột biến'),
      statData('Crossover Rate', 'bi-arrow-repeat', 'bg-orange', scheduleRun.crossoverRate ?? '-', 'Tỷ lệ lai ghép'),
      statData('Tổng penalty', 'bi-exclamation-triangle', 'bg-red', scheduleRun.totalPenalty ?? '-', 'Điểm phạt tổng'),
      statData('Hard constraints', 'bi-shield-check', 'bg-orange', scheduleRun.hardConstraintViolations ?? '-', 'Vi phạm ràng buộc cứng'),
      statData('Soft constraints', 'bi-list-check', 'bg-purple', scheduleRun.softConstraintViolations ?? '-', 'Điểm phạt ràng buộc mềm'),
      statData('Fitness score', 'bi-trophy', 'bg-green', scheduleRun.id ? calcFitness(scheduleRun) : '-', 'Tính từ penalty')
    ])}

    <div class="results-stack">
      <div class="card-panel timetable-result-panel">
        <div class="card-title mb-3">Thời khóa biểu tuần</div>
        ${entries.length ? renderTimetable(entries) : `<div class="empty-state">Chưa có kết quả thời khóa biểu từ API. Hãy vào trang “Tạo thời khóa biểu” để chạy thuật toán.</div>`}
      </div>

      <div class="results-bottom-grid">
        <div class="card-panel fitness-result-card">
          <div class="d-flex align-items-center justify-content-between gap-2 mb-2 flex-wrap">
            <div class="card-title mb-0">Lịch sử Fitness theo lần chạy</div>
            <span class="source-badge"><i class="bi bi-database-check me-1"></i>/api/timetable/runs</span>
          </div>
          <div class="fixed-chart fixed-chart-fitness"><canvas id="fitnessChart"></canvas></div>
        </div>

        <div class="card-panel version-result-card">
          <div class="card-title mb-3">Thông tin phiên bản</div>
          <div class="version-info-grid">
            <div class="version-info-item"><span>Phiên bản</span><b>${scheduleRun.id ? `#${scheduleRun.id}` : '-'}</b></div>
            <div class="version-info-item"><span>Tên lần chạy</span><b>${safeText(scheduleRun.runName)}</b></div>
            <div class="version-info-item"><span>Trạng thái</span><b>${safeText(scheduleRun.status)}</b></div>
            <div class="version-info-item"><span>Thời gian tạo</span><b>${formatDate(scheduleRun.createdAt)}</b></div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mt-1">
      <div class="col-md-6"><div class="card-panel"><div class="card-title">Tổng kết</div>${summaryResult(scheduleRun)}</div></div>
      <div class="col-md-6"><div class="card-panel"><div class="card-title">Ghi chú</div><p class="text-muted mb-0">Không dùng dữ liệu demo. Nếu bảng trống, nghĩa là backend chưa trả kết quả mới nhất hoặc API đang lỗi.</p></div></div>
    </div>
    <footer class="text-center text-muted small mt-4">© 2025 GA Timetable Scheduler. All rights reserved.</footer>
  `;
  renderFitnessChart(runs);
}

function summaryResult(run) {
  if (!run?.id) return `<p class="text-muted mb-0">Chưa có dữ liệu kết quả từ backend.</p>`;
  const hard = n(run.hardConstraintViolations);
  const soft = n(run.softConstraintViolations);
  return `
    <p class="mb-1 ${hard === 0 ? 'text-success' : 'text-danger'}"><i class="bi ${hard === 0 ? 'bi-check-circle' : 'bi-exclamation-triangle'} me-1"></i>Vi phạm ràng buộc cứng: ${hard}</p>
    <p class="mb-1 text-primary"><i class="bi bi-info-circle me-1"></i>Điểm phạt ràng buộc mềm: ${soft}</p>
    <p class="mb-0 text-success"><i class="bi bi-check-circle me-1"></i>Fitness ước tính: ${calcFitness(run)}</p>
  `;
}

function calcFitness(run) {
  if (run?.fitnessScore !== undefined && run?.fitnessScore !== null) return `${Number(run.fitnessScore).toFixed(2)}%`;
  const penalty = Number(run?.totalPenalty ?? 0);
  return `${Math.max(0, (100 - penalty * 0.6)).toFixed(2)}%`;
}


function removeVietnamese(str = '') {
  return String(str)
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D');
}

function renderTimetable(entries = []) {
  const days = [2, 3, 4, 5, 6, 7];

  // Ưu tiên lấy đúng các slot mà backend trả về.
  // Nếu backend chưa trả slot rõ ràng thì hiển thị khung chuẩn 5 ca.
  const slots = [...new Set(entries.map(e => Number(e.slotIndex)).filter(Boolean))].sort((a, b) => a - b);
  const finalSlots = slots.length ? slots : [1, 2, 3, 4, 5];

  return `
    <div class="timetable-grid">
      <table class="timetable" id="resultTimetable">
        <colgroup>
          <col class="ca-col">
          ${days.map(() => '<col class="day-col">').join('')}
        </colgroup>
        <thead>
          <tr>
            <th>Ca học</th>
            ${days.map(d => `<th>${dayName(d)}</th>`).join('')}
          </tr>
        </thead>
        <tbody>
          ${finalSlots.map(slot => `
            <tr>
              <th class="slot-cell">${renderSlotCell(slot)}</th>
              ${days.map(day => renderTimetableCell(entries, day, slot)).join('')}
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}

function renderTimetableCell(entries, day, slot) {
  const found = entries.find(e => Number(e.dayOfWeek) === day && Number(e.slotIndex) === slot);

  if (!found) {
    return `<td class="empty-cell">-</td>`;
  }

  const subject = safeText(found.subjectName || found.subjectCode || found.classCode || 'Lớp học');
  const room = safeText(found.roomName || found.roomCode || '-');
  const teacher = safeText(found.teacherName || found.teacherCode || '-');
  const colorClass = classColorByText(subject);

  return `
    <td>
      <div class="class-cell ${colorClass}" title="${subject} - ${room} - ${teacher}">
        <strong>${subject}</strong>
        <span class="class-room">${room}</span>
        <span class="class-teacher">${teacher}</span>
      </div>
    </td>
  `;
}

function renderSlotCell(slot) {
  const info = slotInfo(slot);
  return `
    <div class="slot-box">
      <span class="slot-session ${info.sessionClass}">${info.session}</span>
      <span class="slot-name">Ca ${slot}</span>
      <span class="slot-time">${info.time}</span>
    </div>
  `;
}

function slotInfo(slot) {
  const map = {
    1: { session: 'Sáng', sessionClass: 'morning', time: '07:30 - 09:15' },
    2: { session: 'Sáng', sessionClass: 'morning', time: '09:30 - 11:15' },
    3: { session: 'Chiều', sessionClass: 'afternoon', time: '13:00 - 14:45' },
    4: { session: 'Chiều', sessionClass: 'afternoon', time: '15:00 - 16:45' },
    5: { session: 'Tối', sessionClass: 'evening', time: '18:00 - 19:45' }
  };
  return map[slot] || { session: 'Khác', sessionClass: 'evening', time: '' };
}

function slotTime(slot) {
  return slotInfo(slot).time;
}

function classColorByText(value = '') {
  const t = removeVietnamese(String(value)).toLowerCase();
  if (t.includes('tieng anh') || t.includes('ngoai ngu')) return 'class-pink';
  if (t.includes('toan')) return 'class-purple';
  if (t.includes('mang') || t.includes('he dieu hanh') || t.includes('kien truc')) return 'class-orange';
  if (t.includes('lap trinh') || t.includes('du lieu') || t.includes('co so')) return 'class-green';
  return 'class-green';
}

function renderFitnessChart(runs = []) {
  destroyCharts();
  const el = document.getElementById('fitnessChart');
  if (!el || !window.Chart) return;
  const sorted = [...runs].sort((a,b)=> new Date(a.createdAt || 0) - new Date(b.createdAt || 0));
  const labels = sorted.map(r => formatDate(r.createdAt));
  const values = sorted.map(r => Number(calcFitness(r).replace('%','')));

  charts.fitness = new Chart(el, {
    type:'line',
    data:{ labels, datasets:[{ label:'Fitness (%)', data: values, tension:.35, fill:true, borderWidth: 3 }] },
    options:{ responsive:true, maintainAspectRatio:false, animation:false, plugins:{legend:{display:false}}, scales:{ y:{ beginAtZero:true, max:100 }} }
  });
}

function destroyCharts() {
  Object.values(charts).forEach(c => { try { c.destroy(); } catch {} });
  charts = {};
}

async function openForm(key, mode = 'create', id = null) {
  const config = resourceConfigs[key];
  modalState = { resourceKey: key, mode, id };
  const title = mode === 'create' ? config.addText : `Cập nhật ${config.title.toLowerCase()}`;
  document.getElementById('formModalTitle').textContent = title;
  document.getElementById('formAlert').innerHTML = '';

  let current = {};
  if (mode === 'edit' && id) {
    try { current = await apiRequest(`${config.endpoint}/${id}`); }
    catch (e) { showToast(e.message, 'danger'); }
  }

  document.getElementById('formFields').innerHTML = config.fields.map(field => `
    <div class="col-md-${field.name === 'note' || field.name === 'description' ? '12' : '6'}">
      <label class="form-label">${field.label}${field.required ? ' <span class="text-danger">*</span>' : ''}</label>
      <input class="form-control" name="${field.name}" type="${field.type || 'text'}" value="${safeAttr(current[field.name] ?? field.value ?? '')}" ${field.required ? 'required' : ''}>
    </div>
  `).join('');

  bootstrap.Modal.getOrCreateInstance(document.getElementById('formModal')).show();
}

function safeAttr(v) { return String(v ?? '').replace(/"/g, '&quot;'); }

document.getElementById('entityForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const { resourceKey, mode, id } = modalState;
  const config = resourceConfigs[resourceKey];
  const form = new FormData(e.target);
  const body = {};
  config.fields.forEach(f => {
    const val = form.get(f.name);
    body[f.name] = f.type === 'number' ? Number(val) : val;
  });

  const alert = document.getElementById('formAlert');
  alert.innerHTML = `<div class="alert alert-info py-2"><span class="spinner-border spinner-border-sm me-2"></span>Đang lưu...</div>`;
  try {
    const endpoint = mode === 'create' ? config.endpoint : `${config.endpoint}/${id}`;
    await apiRequest(endpoint, { method: mode === 'create' ? 'POST' : 'PUT', body: JSON.stringify(body) });
    bootstrap.Modal.getInstance(document.getElementById('formModal')).hide();
    showToast(mode === 'create' ? 'Thêm dữ liệu thành công' : 'Cập nhật dữ liệu thành công', 'success');
    goTo(resourceKey);
  } catch (err) {
    alert.innerHTML = `<div class="alert alert-danger py-2">${err.message}<br><small>Nếu lỗi 403 OPTIONS, hãy sửa CORS backend.</small></div>`;
  }
});

async function deleteEntity(key, id) {
  if (!id) return showToast('Không tìm thấy ID để xóa', 'danger');
  if (!confirm('Bạn có chắc muốn xóa dữ liệu này?')) return;
  const config = resourceConfigs[key];
  try {
    await apiRequest(`${config.endpoint}/${id}`, { method: 'DELETE' });
    showToast('Xóa dữ liệu thành công', 'success');
    goTo(key);
  } catch (err) {
    showToast(err.message, 'danger');
  }
}

function viewRow(row) {
  document.getElementById('formModalTitle').textContent = 'Chi tiết dữ liệu';
  document.getElementById('formFields').innerHTML = `<div class="col-12"><pre class="bg-light rounded-4 p-3 mb-0">${JSON.stringify(row, null, 2)}</pre></div>`;
  document.getElementById('formAlert').innerHTML = '';
  bootstrap.Modal.getOrCreateInstance(document.getElementById('formModal')).show();
}

function showToast(message, type = 'primary') {
  const id = `toast-${Date.now()}`;
  document.getElementById('toastContainer').insertAdjacentHTML('beforeend', `
    <div id="${id}" class="toast align-items-center text-bg-${type} border-0" role="alert">
      <div class="d-flex"><div class="toast-body">${message}</div><button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button></div>
    </div>`);
  const toast = bootstrap.Toast.getOrCreateInstance(document.getElementById(id), { delay: 3200 });
  toast.show();
}

function escapeHtml(value) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function exportTimetableToExcel() {
  const table = document.getElementById('resultTimetable');
  if (!table) return showToast('Chưa có bảng thời khóa biểu để xuất', 'warning');

  const rows = [...table.querySelectorAll('tr')].map(tr =>
    [...tr.children].map(cell => {
      const raw = cell.innerText.replace(/\s*\n\s*/g, '<br>').trim();
      return `<td>${escapeHtml(raw).replace(/&lt;br&gt;/g, '<br>')}</td>`;
    }).join('')
  );

  const html = `
    <!doctype html>
    <html>
      <head>
        <meta charset="UTF-8">
        <style>
          table { border-collapse: collapse; font-family: Arial, sans-serif; }
          td, th { border: 1px solid #bfbfbf; padding: 8px; text-align: center; vertical-align: middle; }
          .title { font-weight: bold; font-size: 18px; text-align: left; }
        </style>
      </head>
      <body>
        <table>
          <tr><td class="title" colspan="7">Thời khóa biểu tuần - GA Timetable Scheduler</td></tr>
          ${rows.map(r => `<tr>${r}</tr>`).join('')}
        </table>
      </body>
    </html>`;

  const blob = new Blob(['\ufeff', html], { type: 'application/vnd.ms-excel;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  const now = new Date().toISOString().slice(0, 10);
  a.href = url;
  a.download = `thoi-khoa-bieu-${now}.xls`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
  showToast('Đã xuất file Excel', 'success');
}

// Giữ alias cũ để tránh lỗi nếu còn nơi nào gọi tên cũ.
function exportTableToCsv() {
  exportTimetableToExcel();
}

function goTo(page) {
  const routes = {
    dashboard: renderDashboard,
    relationships: renderRelationships,
    generate: renderGenerate,
    results: renderResults
  };
  if (routes[page]) routes[page]();
  else renderResourcePage(page);
}

nav.addEventListener('click', (e) => {
  const btn = e.target.closest('[data-page]');
  if (!btn) return;
  goTo(btn.dataset.page);
  sidebar.classList.remove('open');
});

document.getElementById('toggleSidebar').addEventListener('click', () => sidebar.classList.toggle('collapsed'));
document.getElementById('desktopMenu').addEventListener('click', () => sidebar.classList.toggle('collapsed'));
document.getElementById('mobileMenu').addEventListener('click', () => sidebar.classList.toggle('open'));

window.goTo = goTo;
window.openForm = openForm;
window.deleteEntity = deleteEntity;
window.viewRow = viewRow;
window.exportTableToCsv = exportTableToCsv;
window.exportTimetableToExcel = exportTimetableToExcel;
window.changeTablePage = changeTablePage;

renderDashboard();

/* ===================== USER REQUESTED FINAL OVERRIDES =====================
   - Bộ lọc thật theo từng loại dữ liệu
   - Phân trang 5 dòng/trang hoạt động ổn định
   - Biểu đồ dashboard đủ không gian, không bị kéo cao/lỗi canvas
   - Bảng thời khóa biểu các cột/ô bằng nhau, ô ca gọn hơn
============================================================================ */

function normalizeKeyword(value = '') {
  return removeVietnamese(String(value)).toLowerCase().trim();
}

function filterText(value) {
  return value === undefined || value === null || value === '' ? 'Chưa có' : String(value);
}

function uniqueOptions(rows, getter) {
  const values = [...new Set((rows || []).map(getter).filter(v => v !== undefined && v !== null && String(v).trim() !== ''))];
  return values
    .map(v => String(v))
    .sort((a, b) => a.localeCompare(b, 'vi'))
    .map(v => ({ value: v, label: v, test: row => String(getter(row) ?? '') === v }));
}

function getFilterDefs(key, rows = []) {
  const defs = {
    teachers: [
      {
        id: 'contact', label: 'Liên hệ', options: [
          { value: 'hasEmail', label: 'Có email', test: r => !!r.email },
          { value: 'noEmail', label: 'Thiếu email', test: r => !r.email },
          { value: 'hasPhone', label: 'Có SĐT', test: r => !!r.phone },
          { value: 'noPhone', label: 'Thiếu SĐT', test: r => !r.phone }
        ]
      }
    ],
    subjects: [
      { id: 'periods', label: 'Số tiết', options: uniqueOptions(rows, r => r.defaultPeriods ? `${r.defaultPeriods} tiết` : '') }
    ],
    studentGroups: [
      {
        id: 'size', label: 'Sĩ số', options: [
          { value: 'small', label: 'Dưới 35 SV', test: r => n(r.numberOfStudents) < 35 },
          { value: 'medium', label: '35 - 50 SV', test: r => n(r.numberOfStudents) >= 35 && n(r.numberOfStudents) <= 50 },
          { value: 'large', label: 'Trên 50 SV', test: r => n(r.numberOfStudents) > 50 }
        ]
      }
    ],
    rooms: [
      { id: 'building', label: 'Tòa nhà', options: uniqueOptions(rows, r => r.building) },
      {
        id: 'capacity', label: 'Sức chứa', options: [
          { value: 'small', label: 'Dưới 40 chỗ', test: r => n(r.capacity) < 40 },
          { value: 'medium', label: '40 - 80 chỗ', test: r => n(r.capacity) >= 40 && n(r.capacity) <= 80 },
          { value: 'large', label: 'Trên 80 chỗ', test: r => n(r.capacity) > 80 }
        ]
      }
    ],
    equipment: [
      {
        id: 'type', label: 'Loại thiết bị', options: [
          { value: 'projector', label: 'Máy chiếu / màn chiếu', test: r => normalizeKeyword(text(r)).includes('chieu') },
          { value: 'computer', label: 'Máy tính / lab', test: r => normalizeKeyword(text(r)).includes('may') || normalizeKeyword(text(r)).includes('lab') || normalizeKeyword(text(r)).includes('pc') },
          { value: 'network', label: 'Thiết bị mạng', test: r => normalizeKeyword(text(r)).includes('mang') || normalizeKeyword(text(r)).includes('router') || normalizeKeyword(text(r)).includes('switch') },
          { value: 'other', label: 'Khác', test: r => {
              const t = normalizeKeyword(text(r));
              return !t.includes('chieu') && !t.includes('may') && !t.includes('lab') && !t.includes('pc') && !t.includes('mang') && !t.includes('router') && !t.includes('switch');
            }
          }
        ]
      }
    ],
    timeSlots: [
      { id: 'day', label: 'Thứ', options: uniqueOptions(rows, r => r.dayName || dayName(r.dayOfWeek)) },
      { id: 'slot', label: 'Ca học', options: uniqueOptions(rows, r => r.slotIndex ? `Ca ${r.slotIndex}` : '') }
    ],
    courseClasses: [
      { id: 'subject', label: 'Môn học', options: uniqueOptions(rows, r => r.subjectName || r.subjectCode) },
      { id: 'teacher', label: 'Giáo viên', options: uniqueOptions(rows, r => r.teacherName || r.teacherCode) }
    ]
  };
  return (defs[key] || []).filter(def => (def.options || []).length > 0).slice(0, 2);
}

function renderFilter(config, key) {
  const rows = tableState[key]?.rows || [];
  const filterDefs = getFilterDefs(key, rows);
  const filterCols = filterDefs.map(def => `
    <div class="col-lg-2 col-md-6">
      <label class="form-label small text-muted">${def.label}</label>
      <select class="form-select advanced-filter-select advanced-filter" data-filter-id="${def.id}">
        <option value="">Tất cả</option>
        ${def.options.map(opt => `<option value="${safeText(opt.value)}">${safeText(opt.label)}</option>`).join('')}
      </select>
    </div>
  `).join('');

  const searchCol = filterDefs.length >= 2 ? 'col-lg-4' : filterDefs.length === 1 ? 'col-lg-5' : 'col-lg-7';
  const sortCol = filterDefs.length >= 2 ? 'col-lg-2' : 'col-lg-3';

  return `
    <div class="filter-card mb-3">
      <div class="row g-3 align-items-end">
        <div class="${searchCol}">
          <label class="form-label small text-muted">Tìm kiếm</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
            <input id="searchInput" type="text" class="form-control" placeholder="${config.searchPlaceholder || 'Tìm kiếm...'}">
          </div>
        </div>
        ${filterCols}
        <div class="${sortCol} col-md-6">
          <label class="form-label small text-muted">Sắp xếp</label>
          <select id="sortSelect" class="form-select">
            <option value="default">Mặc định</option>
            <option value="az">Tên / mã A-Z</option>
            <option value="za">Tên / mã Z-A</option>
            <option value="newest">Mới nhất</option>
            <option value="oldest">Cũ nhất</option>
          </select>
        </div>
        <div class="col-lg-2 col-md-6 d-grid">
          <label class="form-label small text-muted">Thao tác</label>
          <button id="resetFilterBtn" class="btn btn-light border btn-reset-filter" type="button" title="Xóa tìm kiếm, bộ lọc và sắp xếp về mặc định">
            <i class="bi bi-arrow-counterclockwise me-1"></i>Đặt lại
          </button>
        </div>
      </div>
    </div>
  `;
}

function getFilteredRows(key) {
  const state = tableState[key] || { rows: [], search: '', sort: 'default', page: 1, filters: {} };
  const q = normalizeKeyword(state.search || '');
  const filterDefs = getFilterDefs(key, state.rows || []);
  let filtered = (state.rows || []).filter(row => !q || normalizeKeyword(text(row)).includes(q));

  filterDefs.forEach(def => {
    const selected = state.filters?.[def.id];
    if (!selected) return;
    const option = def.options.find(opt => String(opt.value) === String(selected));
    if (option?.test) filtered = filtered.filter(option.test);
  });

  return sortRows(filtered, state.sort);
}

function setupSearchFilter(key) {
  const input = document.getElementById('searchInput');
  const sort = document.getElementById('sortSelect');
  const reset = document.getElementById('resetFilterBtn');
  const filterSelects = [...document.querySelectorAll('.advanced-filter')];

  if (!tableState[key]) return;
  tableState[key].filters = tableState[key].filters || {};

  if (input) input.addEventListener('input', () => {
    tableState[key].search = input.value;
    tableState[key].page = 1;
    refreshResourceTable(key);
  });

  filterSelects.forEach(select => {
    select.addEventListener('change', () => {
      tableState[key].filters[select.dataset.filterId] = select.value;
      tableState[key].page = 1;
      refreshResourceTable(key);
    });
  });

  if (sort) sort.addEventListener('change', () => {
    tableState[key].sort = sort.value;
    tableState[key].page = 1;
    refreshResourceTable(key);
  });

  if (reset) reset.addEventListener('click', () => {
    tableState[key].search = '';
    tableState[key].sort = 'default';
    tableState[key].filters = {};
    tableState[key].page = 1;
    if (input) input.value = '';
    if (sort) sort.value = 'default';
    filterSelects.forEach(select => { select.value = ''; });
    refreshResourceTable(key);
  });
}

function renderDashboardCharts(scheduleRuns, roomUsage) {
  destroyCharts();
  if (!window.Chart) return;

  const weeklyData = groupRunsLast7Days(scheduleRuns);
  const weekly = document.getElementById('weeklyChart');
  if (weekly) {
    const maxWeekly = Math.max(1, ...weeklyData.values);
    charts.weekly = new Chart(weekly, {
      type: 'line',
      data: {
        labels: weeklyData.labels,
        datasets: [{
          label: 'Số lần tạo lịch',
          data: weeklyData.values,
          tension: .38,
          fill: true,
          borderWidth: 3,
          borderColor: '#3aa0e6',
          backgroundColor: 'rgba(58, 160, 230, .36)',
          pointBackgroundColor: '#3aa0e6',
          pointBorderColor: '#3aa0e6',
          pointRadius: 4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation: false,
        normalized: true,
        resizeDelay: 180,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, suggestedMax: Math.max(5, maxWeekly + 1), ticks: { precision: 0 }, grid: { color: 'rgba(15,23,42,.10)' } },
          x: { grid: { color: 'rgba(15,23,42,.08)' } }
        }
      }
    });
  }

  const room = document.getElementById('roomChart');
  if (room) {
    const total = roomUsage.used + roomUsage.free + roomUsage.maintenance;
    charts.room = new Chart(room, {
      type: 'doughnut',
      data: {
        labels: ['Đang sử dụng', 'Trống', 'Bảo trì'],
        datasets: [{
          data: total ? [roomUsage.used, roomUsage.free, roomUsage.maintenance] : [1],
          backgroundColor: total ? ['#36a2eb', '#12b76a', '#ff8a00'] : ['#edf0f5'],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation: false,
        resizeDelay: 180,
        plugins: { legend: { display: false } },
        cutout: '66%'
      }
    });
  }
}

function renderTimetable(entries = []) {
  const days = [2, 3, 4, 5, 6, 7];
  const slots = [...new Set(entries.map(e => Number(e.slotIndex)).filter(Boolean))].sort((a, b) => a - b);
  const finalSlots = slots.length ? slots : [1, 2, 3, 4, 5];

  return `
    <div class="timetable-grid">
      <table class="timetable" id="resultTimetable">
        <colgroup>
          <col class="ca-col">
          ${days.map(() => '<col class="day-col">').join('')}
        </colgroup>
        <thead>
          <tr>
            <th>Ca học</th>
            ${days.map(d => `<th>${dayName(d)}</th>`).join('')}
          </tr>
        </thead>
        <tbody>
          ${finalSlots.map(slot => `
            <tr>
              <th class="slot-cell">${renderSlotCell(slot)}</th>
              ${days.map(day => renderTimetableCell(entries, day, slot)).join('')}
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}

function renderTimetableCell(entries, day, slot) {
  const found = entries.find(e => Number(e.dayOfWeek) === day && Number(e.slotIndex) === slot);
  if (!found) return `<td class="empty-cell">-</td>`;

  const subject = safeText(found.subjectName || found.subjectCode || found.classCode || 'Lớp học');
  const room = safeText(found.roomName || found.roomCode || '-');
  const teacher = safeText(found.teacherName || found.teacherCode || '-');
  const colorClass = classColorByText(subject);

  return `
    <td>
      <div class="class-cell ${colorClass}" title="${subject} - ${room} - ${teacher}">
        <strong>${subject}</strong>
        <span class="class-room">${room}</span>
        <span class="class-teacher">${teacher}</span>
      </div>
    </td>
  `;
}

function renderSlotCell(slot) {
  const info = slotInfo(slot);
  return `
    <div class="slot-box">
      <span class="slot-session ${info.sessionClass}">${info.session}</span>
      <span class="slot-name">Ca ${slot}</span>
      <span class="slot-time">${info.time}</span>
    </div>
  `;
}

function slotInfo(slot) {
  const map = {
    1: { session: 'Sáng', sessionClass: 'morning', time: '07:30 - 09:15' },
    2: { session: 'Sáng', sessionClass: 'morning', time: '09:30 - 11:15' },
    3: { session: 'Chiều', sessionClass: 'afternoon', time: '13:00 - 14:45' },
    4: { session: 'Chiều', sessionClass: 'afternoon', time: '15:00 - 16:45' },
    5: { session: 'Tối', sessionClass: 'evening', time: '18:00 - 19:45' }
  };
  return map[slot] || { session: 'Khác', sessionClass: 'evening', time: '' };
}
