# Tài liệu API Backend - GA Timetable Scheduler

Tài liệu này mô tả các API backend của hệ thống **GA Timetable Scheduler** dùng để frontend tích hợp. Backend được xây dựng bằng Spring Boot, SQL Server và thuật toán di truyền Genetic Algorithm để tạo thời khóa biểu tự động.

## 1. Thông tin chung

### Base URL local

```http
http://localhost:8080
```

### Content-Type

Các API `POST` và `PUT` gửi dữ liệu dạng JSON:

```http
Content-Type: application/json
```

### Format response chung

Hầu hết API trả về dạng:

```json
{
  "success": true,
  "message": "Thao tác thành công",
  "data": {}
}
```

Khi có lỗi, backend có thể trả về:

```json
{
  "success": false,
  "message": "Nội dung lỗi",
  "data": null
}
```

### Ghi chú về ID

Các trường như `teacherId`, `subjectId`, `roomId`, `timeSlotId` là ID đã tồn tại trong database. Frontend nên gọi API danh sách trước để lấy ID rồi mới gửi request tạo dữ liệu liên quan.

---

# 2. API kiểm tra server

## 2.1. Kiểm tra backend có chạy không

```http
GET /
```

### Mục đích

Dùng để kiểm tra nhanh server backend đã chạy hay chưa.

---

# 3. Teacher API - Quản lý giáo viên

Nhóm API này dùng để thêm, sửa, xóa, xem danh sách giáo viên.

## 3.1. Lấy danh sách giáo viên

```http
GET /api/teachers
```

### Mục đích

Frontend dùng API này để hiển thị danh sách giáo viên trong màn hình quản lý giáo viên hoặc dropdown chọn giáo viên khi tạo lớp học phần.

---

## 3.2. Lấy giáo viên theo ID

```http
GET /api/teachers/{id}
```

### Mục đích

Dùng để xem chi tiết một giáo viên hoặc load dữ liệu lên form chỉnh sửa.

---

## 3.3. Tạo giáo viên

```http
POST /api/teachers
```

### Body mẫu

```json
{
  "teacherCode": "GV_TEST",
  "fullName": "Giáo viên Test",
  "email": "gvtest@example.com",
  "phone": "0900000000"
}
```

### Ý nghĩa field

| Field | Kiểu dữ liệu | Bắt buộc | Mô tả |
|---|---|---|---|
| `teacherCode` | String | Có | Mã giáo viên, không được trùng |
| `fullName` | String | Có | Họ tên giáo viên |
| `email` | String | Không | Email giáo viên |
| `phone` | String | Không | Số điện thoại |

---

## 3.4. Cập nhật giáo viên

```http
PUT /api/teachers/{id}
```

### Body mẫu

```json
{
  "teacherCode": "GV_TEST_UPD",
  "fullName": "Giáo viên Test Updated",
  "email": "gvtest.updated@example.com",
  "phone": "0911111111"
}
```

### Mục đích

Cập nhật thông tin giáo viên theo ID.

---

## 3.5. Xóa giáo viên

```http
DELETE /api/teachers/{id}
```

### Mục đích

Xóa giáo viên khỏi hệ thống. Nếu giáo viên đang được dùng trong lớp học phần hoặc bảng quan hệ, database có thể không cho xóa do ràng buộc khóa ngoại.

---

# 4. Subject API - Quản lý môn học

Nhóm API này dùng để quản lý danh sách môn học.

## 4.1. Lấy danh sách môn học

```http
GET /api/subjects
```

### Mục đích

Hiển thị danh sách môn học và dùng cho dropdown khi tạo lớp học phần.

---

## 4.2. Lấy môn học theo ID

```http
GET /api/subjects/{id}
```

---

## 4.3. Tạo môn học

```http
POST /api/subjects
```

### Body mẫu

```json
{
  "subjectCode": "TEST_SUBJECT",
  "subjectName": "Môn học Test",
  "defaultPeriods": 2
}
```

### Ý nghĩa field

| Field | Kiểu dữ liệu | Bắt buộc | Mô tả |
|---|---|---|---|
| `subjectCode` | String | Có | Mã môn học, không được trùng |
| `subjectName` | String | Có | Tên môn học |
| `defaultPeriods` | Integer | Có | Số tiết mặc định của môn học |

---

## 4.4. Cập nhật môn học

```http
PUT /api/subjects/{id}
```

### Body mẫu

```json
{
  "subjectCode": "TEST_SUBJECT_UPD",
  "subjectName": "Môn học Test Updated",
  "defaultPeriods": 3
}
```

---

## 4.5. Xóa môn học

```http
DELETE /api/subjects/{id}
```

---

# 5. Student Group API - Quản lý nhóm sinh viên

Nhóm sinh viên là lớp hoặc nhóm học chung một thời khóa biểu.

## 5.1. Lấy danh sách nhóm sinh viên

```http
GET /api/student-groups
```

### Mục đích

Dùng để hiển thị danh sách nhóm sinh viên và chọn nhóm khi tạo lớp học phần.

---

## 5.2. Lấy nhóm sinh viên theo ID

```http
GET /api/student-groups/{id}
```

---

## 5.3. Tạo nhóm sinh viên

```http
POST /api/student-groups
```

### Body mẫu

```json
{
  "groupCode": "K18-TEST",
  "groupName": "K18 Test Group",
  "numberOfStudents": 45
}
```

### Ý nghĩa field

| Field | Kiểu dữ liệu | Bắt buộc | Mô tả |
|---|---|---|---|
| `groupCode` | String | Có | Mã nhóm sinh viên, không được trùng |
| `groupName` | String | Có | Tên nhóm sinh viên |
| `numberOfStudents` | Integer | Có | Số lượng sinh viên trong nhóm |

---

## 5.4. Cập nhật nhóm sinh viên

```http
PUT /api/student-groups/{id}
```

### Body mẫu

```json
{
  "groupCode": "K18-TEST-UPD",
  "groupName": "K18 Test Group Updated",
  "numberOfStudents": 50
}
```

---

## 5.5. Xóa nhóm sinh viên

```http
DELETE /api/student-groups/{id}
```

---

# 6. Room API - Quản lý phòng học

Nhóm API này dùng để quản lý phòng học, sức chứa và vị trí phòng.

## 6.1. Lấy danh sách phòng học

```http
GET /api/rooms
```

### Mục đích

Dùng để hiển thị danh sách phòng học, chọn phòng, hoặc làm dữ liệu đầu vào cho thuật toán GA.

---

## 6.2. Lấy phòng học theo ID

```http
GET /api/rooms/{id}
```

---

## 6.3. Tạo phòng học

```http
POST /api/rooms
```

### Body mẫu

```json
{
  "roomCode": "P_TEST",
  "roomName": "Phòng Test",
  "capacity": 60,
  "building": "Tòa Test",
  "floorNumber": 1
}
```

### Ý nghĩa field

| Field | Kiểu dữ liệu | Bắt buộc | Mô tả |
|---|---|---|---|
| `roomCode` | String | Có | Mã phòng, không được trùng |
| `roomName` | String | Có | Tên phòng |
| `capacity` | Integer | Có | Sức chứa phòng |
| `building` | String | Không | Tòa nhà |
| `floorNumber` | Integer | Không | Số tầng |

---

## 6.4. Cập nhật phòng học

```http
PUT /api/rooms/{id}
```

### Body mẫu

```json
{
  "roomCode": "P_TEST_UPD",
  "roomName": "Phòng Test Updated",
  "capacity": 70,
  "building": "Tòa Test",
  "floorNumber": 2
}
```

---

## 6.5. Xóa phòng học

```http
DELETE /api/rooms/{id}
```

---

# 7. Equipment API - Quản lý thiết bị

Thiết bị là các tài nguyên như máy chiếu, máy tính, phòng lab, loa, micro.

## 7.1. Lấy danh sách thiết bị

```http
GET /api/equipment
```

---

## 7.2. Lấy thiết bị theo ID

```http
GET /api/equipment/{id}
```

---

## 7.3. Tạo thiết bị

```http
POST /api/equipment
```

### Body mẫu

```json
{
  "equipmentCode": "TEST_EQUIP",
  "equipmentName": "Thiết bị Test"
}
```

### Ý nghĩa field

| Field | Kiểu dữ liệu | Bắt buộc | Mô tả |
|---|---|---|---|
| `equipmentCode` | String | Có | Mã thiết bị, không được trùng |
| `equipmentName` | String | Có | Tên thiết bị |

---

## 7.4. Cập nhật thiết bị

```http
PUT /api/equipment/{id}
```

### Body mẫu

```json
{
  "equipmentCode": "TEST_EQUIP_UPD",
  "equipmentName": "Thiết bị Test Updated"
}
```

---

## 7.5. Xóa thiết bị

```http
DELETE /api/equipment/{id}
```

---

# 8. TimeSlot API - Quản lý khung giờ học

Khung giờ là thứ trong tuần và ca học cụ thể.

## 8.1. Lấy danh sách khung giờ

```http
GET /api/timeslots
```

### Mục đích

Dùng để frontend hiển thị danh sách ca học và làm dữ liệu đầu vào cho thuật toán xếp lịch.

---

## 8.2. Lấy khung giờ theo ID

```http
GET /api/timeslots/{id}
```

---

## 8.3. Tạo khung giờ

```http
POST /api/timeslots
```

### Body mẫu

```json
{
  "dayOfWeek": 7,
  "slotIndex": 5,
  "startTime": "18:00:00",
  "endTime": "20:00:00",
  "description": "Thứ 7 - Ca test"
}
```

### Ý nghĩa field

| Field | Kiểu dữ liệu | Bắt buộc | Mô tả |
|---|---|---|---|
| `dayOfWeek` | Integer | Có | Thứ trong tuần, từ 2 đến 7 |
| `slotIndex` | Integer | Có | Số thứ tự ca học trong ngày |
| `startTime` | String | Có | Giờ bắt đầu, dạng `HH:mm:ss` |
| `endTime` | String | Có | Giờ kết thúc, dạng `HH:mm:ss` |
| `description` | String | Không | Mô tả khung giờ |

### Quy ước `dayOfWeek`

| Giá trị | Ý nghĩa |
|---:|---|
| 2 | Thứ 2 |
| 3 | Thứ 3 |
| 4 | Thứ 4 |
| 5 | Thứ 5 |
| 6 | Thứ 6 |
| 7 | Thứ 7 |

---

## 8.4. Cập nhật khung giờ

```http
PUT /api/timeslots/{id}
```

### Body mẫu

```json
{
  "dayOfWeek": 7,
  "slotIndex": 6,
  "startTime": "20:00:00",
  "endTime": "21:30:00",
  "description": "Thứ 7 - Ca test updated"
}
```

---

## 8.5. Xóa khung giờ

```http
DELETE /api/timeslots/{id}
```

---

# 9. Course Class API - Quản lý lớp học phần

Lớp học phần là đơn vị chính cần được thuật toán GA xếp lịch.

## 9.1. Lấy danh sách lớp học phần

```http
GET /api/course-classes
```

### Mục đích

Dùng để hiển thị danh sách lớp học phần cần xếp lịch.

---

## 9.2. Lấy lớp học phần theo ID

```http
GET /api/course-classes/{id}
```

---

## 9.3. Tạo lớp học phần

```http
POST /api/course-classes
```

### Body mẫu

```json
{
  "classCode": "TEST-CLASS-01",
  "subjectId": 1,
  "teacherId": 1,
  "studentGroupId": 1,
  "numberOfStudents": 45,
  "periods": 2,
  "note": "Lớp học phần test"
}
```

### Ý nghĩa field

| Field | Kiểu dữ liệu | Bắt buộc | Mô tả |
|---|---|---|---|
| `classCode` | String | Có | Mã lớp học phần, không được trùng |
| `subjectId` | Integer | Có | ID môn học |
| `teacherId` | Integer | Có | ID giáo viên phụ trách |
| `studentGroupId` | Integer | Có | ID nhóm sinh viên học lớp này |
| `numberOfStudents` | Integer | Có | Số lượng sinh viên của lớp học phần |
| `periods` | Integer | Có | Số tiết học |
| `note` | String | Không | Ghi chú |

### Lưu ý frontend

Trước khi tạo lớp học phần, frontend nên gọi:

```http
GET /api/subjects
GET /api/teachers
GET /api/student-groups
```

để lấy danh sách `subjectId`, `teacherId`, `studentGroupId`.

---

## 9.4. Cập nhật lớp học phần

```http
PUT /api/course-classes/{id}
```

### Body mẫu

```json
{
  "classCode": "TEST-CLASS-01-UPD",
  "subjectId": 1,
  "teacherId": 1,
  "studentGroupId": 1,
  "numberOfStudents": 50,
  "periods": 2,
  "note": "Lớp học phần test updated"
}
```

---

## 9.5. Xóa lớp học phần

```http
DELETE /api/course-classes/{id}
```

---

# 10. Relationship API - Quản lý dữ liệu quan hệ

Các API này dùng để khai báo ràng buộc và thông tin phụ trợ cho thuật toán GA.

---

## 10.1. Teacher Subjects - Giáo viên dạy được môn nào

### Lấy danh sách

```http
GET /api/teacher-subjects
```

### Tạo quan hệ giáo viên - môn học

```http
POST /api/teacher-subjects
```

### Body mẫu

```json
{
  "teacherId": 1,
  "subjectId": 1
}
```

### Mục đích

Cho biết giáo viên nào có thể dạy môn nào. Dữ liệu này giúp kiểm tra tính hợp lệ khi tạo lớp học phần hoặc khi mở rộng thuật toán xếp lịch.

### Xóa

```http
DELETE /api/teacher-subjects/{id}
```

---

## 10.2. Teacher Available Times - Thời gian giáo viên rảnh

### Lấy danh sách

```http
GET /api/teacher-available-times
```

### Tạo thời gian rảnh cho giáo viên

```http
POST /api/teacher-available-times
```

### Body mẫu

```json
{
  "teacherId": 1,
  "timeSlotId": 1
}
```

### Mục đích

Cho biết giáo viên có thể dạy ở khung giờ nào. Đây là dữ liệu quan trọng cho yêu cầu cứng: giáo viên không được bị xếp vào thời gian không rảnh.

### Xóa

```http
DELETE /api/teacher-available-times/{id}
```

---

## 10.3. Teacher Preferred Times - Thời gian giáo viên mong muốn

### Lấy danh sách

```http
GET /api/teacher-preferred-times
```

### Tạo thời gian mong muốn

```http
POST /api/teacher-preferred-times
```

### Body mẫu

```json
{
  "teacherId": 1,
  "timeSlotId": 1,
  "priorityScore": 10
}
```

### Mục đích

Cho biết giáo viên ưu tiên dạy ở khung giờ nào. Đây là yêu cầu mềm, giúp thuật toán GA tối ưu lịch tốt hơn.

| Field | Mô tả |
|---|---|
| `teacherId` | ID giáo viên |
| `timeSlotId` | ID khung giờ |
| `priorityScore` | Điểm ưu tiên, càng cao càng tốt |

### Xóa

```http
DELETE /api/teacher-preferred-times/{id}
```

---

## 10.4. Room Available Times - Thời gian phòng trống

### Lấy danh sách

```http
GET /api/room-available-times
```

### Tạo thời gian trống cho phòng

```http
POST /api/room-available-times
```

### Body mẫu

```json
{
  "roomId": 1,
  "timeSlotId": 1
}
```

### Mục đích

Cho biết phòng học có thể sử dụng ở khung giờ nào. Đây là dữ liệu dùng cho yêu cầu cứng: một lớp chỉ được xếp vào phòng đang trống.

### Xóa

```http
DELETE /api/room-available-times/{id}
```

---

## 10.5. Room Equipment - Thiết bị của phòng học

### Lấy danh sách

```http
GET /api/room-equipment
```

### Thêm thiết bị cho phòng

```http
POST /api/room-equipment
```

### Body mẫu

```json
{
  "roomId": 1,
  "equipmentId": 1
}
```

### Mục đích

Khai báo phòng học có thiết bị nào. Ví dụ phòng có máy chiếu, máy tính, phòng lab. Thuật toán dùng dữ liệu này để kiểm tra phòng có đáp ứng yêu cầu thiết bị của lớp học phần không.

### Xóa

```http
DELETE /api/room-equipment/{id}
```

---

## 10.6. Course Class Required Equipment - Thiết bị lớp học phần yêu cầu

### Lấy danh sách

```http
GET /api/course-class-required-equipment
```

### Thêm thiết bị yêu cầu cho lớp học phần

```http
POST /api/course-class-required-equipment
```

### Body mẫu

```json
{
  "courseClassId": 1,
  "equipmentId": 1
}
```

### Mục đích

Khai báo lớp học phần yêu cầu thiết bị nào. Ví dụ lớp thực hành Java cần phòng máy, môn lý thuyết cần máy chiếu. Thuật toán GA dùng dữ liệu này để chọn phòng phù hợp.

### Xóa

```http
DELETE /api/course-class-required-equipment/{id}
```

---

# 11. Timetable API - Tạo và xem thời khóa biểu

Đây là nhóm API quan trọng nhất của hệ thống. Frontend dùng nhóm API này để chạy thuật toán GA và hiển thị kết quả thời khóa biểu.

---

## 11.1. Tạo thời khóa biểu tự động

```http
POST /api/timetable/generate
```

### Body mẫu

```json
{
  "runName": "Demo GA Run",
  "populationSize": 100,
  "generations": 500,
  "mutationRate": 0.05,
  "crossoverRate": 0.8
}
```

### Ý nghĩa field

| Field | Kiểu dữ liệu | Bắt buộc | Mô tả |
|---|---|---|---|
| `runName` | String | Không | Tên lần chạy thuật toán |
| `populationSize` | Integer | Có | Số lượng cá thể trong quần thể GA |
| `generations` | Integer | Có | Số thế hệ thuật toán sẽ lặp |
| `mutationRate` | Double | Có | Tỷ lệ đột biến, từ 0 đến 1 |
| `crossoverRate` | Double | Có | Tỷ lệ lai ghép, từ 0 đến 1 |

### Mục đích

Khi frontend gọi API này, backend sẽ:

1. Lấy danh sách lớp học phần, phòng học, giáo viên, nhóm sinh viên, khung giờ.
2. Tạo nhiều phương án thời khóa biểu ban đầu.
3. Tính điểm phạt dựa trên ràng buộc cứng và ràng buộc mềm.
4. Dùng thuật toán Genetic Algorithm để chọn, lai ghép, đột biến và tối ưu lịch.
5. Lưu kết quả vào database.
6. Trả về thời khóa biểu tốt nhất cho frontend.

### Frontend nên làm gì với response?

Frontend nên lấy danh sách `timetableEntries` để hiển thị thành bảng thời khóa biểu gồm:

```text
Thứ, ca học, môn học, giáo viên, nhóm sinh viên, phòng học, điểm phạt
```

---

## 11.2. Lấy thời khóa biểu mới nhất

```http
GET /api/timetable/latest
```

### Mục đích

Dùng để hiển thị lại kết quả thời khóa biểu của lần chạy gần nhất mà không cần chạy lại thuật toán.

---

## 11.3. Lấy danh sách các lần chạy thuật toán

```http
GET /api/timetable/runs
```

### Mục đích

Dùng để hiển thị lịch sử các lần tạo thời khóa biểu.

---

## 11.4. Lấy thời khóa biểu theo lần chạy

```http
GET /api/timetable/runs/{runId}
```

### Mục đích

Dùng để xem lại kết quả thời khóa biểu của một lần chạy cụ thể.

---

# 12. Thứ tự frontend nên gọi API khi nhập dữ liệu

Frontend nên nhập dữ liệu theo thứ tự sau để tránh thiếu ID liên kết:

```text
1. Tạo thiết bị
2. Tạo phòng học
3. Gán thiết bị cho phòng học
4. Tạo môn học
5. Tạo giáo viên
6. Gán giáo viên dạy được môn học
7. Tạo nhóm sinh viên
8. Tạo khung giờ học
9. Gán thời gian rảnh cho giáo viên
10. Gán thời gian mong muốn cho giáo viên
11. Gán thời gian trống cho phòng học
12. Tạo lớp học phần
13. Gán thiết bị yêu cầu cho lớp học phần
14. Gọi API tạo thời khóa biểu
15. Hiển thị kết quả thời khóa biểu
```

---

# 13. Gợi ý màn hình frontend

Frontend có thể chia các màn hình như sau:

| Màn hình | API chính cần dùng | Mục đích |
|---|---|---|
| Quản lý giáo viên | `/api/teachers` | Thêm/sửa/xóa giáo viên |
| Quản lý môn học | `/api/subjects` | Thêm/sửa/xóa môn học |
| Quản lý nhóm sinh viên | `/api/student-groups` | Thêm/sửa/xóa nhóm sinh viên |
| Quản lý phòng học | `/api/rooms` | Thêm/sửa/xóa phòng học |
| Quản lý thiết bị | `/api/equipment` | Thêm/sửa/xóa thiết bị |
| Quản lý khung giờ | `/api/timeslots` | Thêm/sửa/xóa ca học |
| Quản lý lớp học phần | `/api/course-classes` | Tạo dữ liệu cần xếp lịch |
| Cấu hình ràng buộc | Relationship API | Gán thiết bị, thời gian rảnh, thời gian mong muốn |
| Tạo thời khóa biểu | `/api/timetable/generate` | Chạy thuật toán GA |
| Xem thời khóa biểu | `/api/timetable/latest` hoặc `/api/timetable/runs/{runId}` | Hiển thị kết quả |

---

# 14. Lưu ý khi frontend test API

- Nên chạy backend trước tại `http://localhost:8080`.
- Nếu gọi API từ frontend khác port, backend đã có CORS config để cho phép gọi API.
- Nếu tạo dữ liệu bị lỗi trùng mã, hãy đổi các field code như `teacherCode`, `subjectCode`, `roomCode`, `classCode`.
- Nếu xóa dữ liệu bị lỗi, có thể dữ liệu đó đang được bảng khác tham chiếu bằng khóa ngoại.
- API tạo thời khóa biểu cần có đủ dữ liệu đầu vào: lớp học phần, phòng học, khung giờ, giáo viên, nhóm sinh viên.

---

# 15. Checklist dữ liệu tối thiểu để chạy tạo lịch

Trước khi gọi:

```http
POST /api/timetable/generate
```

Database nên có ít nhất:

```text
- 1 giáo viên
- 1 môn học
- 1 nhóm sinh viên
- 1 phòng học
- 1 khung giờ
- 1 lớp học phần
- Phòng học có thời gian trống
- Giáo viên có thời gian rảnh
```

Nếu muốn test đúng yêu cầu thiết bị, cần thêm:

```text
- Thiết bị
- Thiết bị của phòng học
- Thiết bị lớp học phần yêu cầu
```

---

# 16. Commit gợi ý cho tài liệu này

```bash
git add docs/API_DOCUMENTATION_VI.md
git commit -m "docs(api): add Vietnamese backend API documentation"
```
