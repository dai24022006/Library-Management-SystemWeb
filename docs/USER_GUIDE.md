# User Guide — Library Management System

## 1. Giới thiệu

Library Management System là hệ thống hỗ trợ quản lý sách, độc giả và hoạt động mượn, trả sách trong thư viện.

Tài liệu này hướng dẫn người dùng thực hiện các nghiệp vụ chính trên giao diện hệ thống.

## 2. Yêu cầu trước khi sử dụng

Trước khi sử dụng hệ thống, cần bảo đảm:

* Backend Spring Boot đã được khởi động.
* Cơ sở dữ liệu MySQL đã được cấu hình và kết nối thành công.
* Frontend đã được mở theo cách triển khai của dự án.
* Các dịch vụ cần thiết đang hoạt động bình thường.

## 3. Quản lý sách

### 3.1. Xem danh sách sách

1. Mở chức năng quản lý sách.
2. Xem danh sách sách được hiển thị trên giao diện.
3. Sử dụng chức năng tìm kiếm hoặc lọc nếu cần tìm một cuốn sách cụ thể.

### 3.2. Thêm sách

1. Mở chức năng thêm sách.
2. Nhập các thông tin cần thiết, chẳng hạn mã sách, tên sách, tác giả, nhà xuất bản, năm xuất bản, thể loại, giá và số lượng.
3. Kiểm tra thông tin đã nhập.
4. Xác nhận lưu dữ liệu.

Hệ thống kiểm tra dữ liệu theo các quy tắc nghiệp vụ trước khi lưu.

### 3.3. Cập nhật sách

1. Tìm sách cần chỉnh sửa.
2. Mở chức năng cập nhật.
3. Thay đổi các thông tin cần thiết.
4. Lưu thay đổi.

### 3.4. Xóa sách

1. Tìm sách cần xóa.
2. Chọn chức năng xóa.
3. Xác nhận thao tác nếu hệ thống yêu cầu.

Nếu sử dụng cơ chế xóa mềm, bản ghi được đánh dấu đã xóa thay vì bị xóa trực tiếp khỏi cơ sở dữ liệu.

## 4. Quản lý độc giả

### 4.1. Xem và tìm kiếm độc giả

1. Mở chức năng quản lý độc giả.
2. Xem danh sách độc giả.
3. Nhập thông tin tìm kiếm theo các trường mà giao diện hỗ trợ.

### 4.2. Thêm độc giả

1. Mở biểu mẫu thêm độc giả.
2. Nhập mã độc giả, họ tên, ngày sinh, giới tính và số điện thoại theo các trường được cung cấp.
3. Kiểm tra dữ liệu.
4. Lưu thông tin.

### 4.3. Cập nhật hoặc xóa độc giả

1. Tìm độc giả cần thao tác.
2. Chọn chức năng cập nhật hoặc xóa.
3. Thực hiện thay đổi và xác nhận theo yêu cầu của giao diện.

Cần kiểm tra các ràng buộc nghiệp vụ liên quan đến độc giả trước khi thực hiện thao tác.

## 5. Quản lý mượn sách

### 5.1. Tạo phiếu mượn

1. Mở chức năng lập phiếu mượn.
2. Chọn độc giả cần mượn sách.
3. Chọn một hoặc nhiều đầu sách.
4. Nhập số lượng cho từng đầu sách.
5. Kiểm tra thông tin phiếu mượn.
6. Xác nhận tạo phiếu.

Hệ thống kiểm tra thông tin độc giả, sách và số lượng theo các quy tắc nghiệp vụ trước khi lưu phiếu.

### 5.2. Xem danh sách phiếu mượn

Người dùng có thể xem danh sách phiếu mượn và sử dụng các chức năng tìm kiếm, lọc hoặc sắp xếp được cung cấp trên giao diện.

Các nhóm phiếu có thể bao gồm:

* Phiếu đang mượn.
* Phiếu đã trả.
* Phiếu quá hạn.
* Tất cả phiếu mượn.

Trạng thái hiển thị phụ thuộc vào dữ liệu và quy tắc nghiệp vụ của hệ thống.

### 5.3. Xem chi tiết phiếu mượn

Chọn một phiếu mượn để xem thông tin như:

* Mã phiếu mượn.
* Thông tin độc giả.
* Ngày mượn.
* Hạn trả.
* Danh sách sách và số lượng tương ứng.
* Trạng thái phiếu.

## 6. Quản lý trả sách

### 6.1. Thực hiện trả sách

1. Tìm phiếu mượn cần trả.
2. Mở chức năng trả sách.
3. Kiểm tra danh sách sách thuộc phiếu.
4. Nhập thông tin xử lý đối với từng đầu sách theo giao diện.
5. Kiểm tra thông tin trước khi xác nhận trả sách.
6. Xác nhận thao tác.

Hệ thống cập nhật dữ liệu trả sách và trạng thái phiếu theo quy tắc nghiệp vụ.

### 6.2. Xử lý sách hỏng hoặc mất

Khi có sách hỏng hoặc mất, người dùng cần ghi nhận đúng số lượng tương ứng theo biểu mẫu trả sách.

Thông tin này được sử dụng để xử lý nghiệp vụ và tính phí phát sinh nếu có.

### 6.3. Theo dõi trạng thái sau khi trả

Sau khi hoàn tất nghiệp vụ, người dùng có thể kiểm tra lại phiếu để xác nhận trạng thái và thông tin trả sách đã được cập nhật.

## 7. Phí phát sinh và thanh toán

Nếu nghiệp vụ tính phí đã được triển khai, người dùng thực hiện theo quy trình:

1. Mở chức năng trả sách.
2. Kiểm tra các khoản phí được hệ thống tính toán.
3. Xem chi tiết từng khoản, số lượng và thành tiền.
4. Kiểm tra tổng phí.
5. Thực hiện thanh toán theo phương thức được hệ thống hỗ trợ.
6. Kiểm tra trạng thái thanh toán sau khi hoàn tất.

Các khoản phí và điều kiện thanh toán phải tuân theo quy định được cấu hình trong hệ thống.

## 8. Tìm kiếm, lọc và sắp xếp

Đối với những màn hình có hỗ trợ, người dùng có thể:

* Tìm kiếm theo thông tin được cung cấp trên giao diện.
* Lọc danh sách theo trạng thái.
* Sắp xếp theo các trường được hỗ trợ.
* Chuyển trang để xem thêm dữ liệu.

Nếu không tìm thấy dữ liệu, hãy kiểm tra từ khóa, điều kiện lọc và trạng thái đang chọn.

## 9. Xử lý lỗi thường gặp

### Không tải được dữ liệu

* Kiểm tra backend có đang chạy không.
* Kiểm tra kết nối MySQL.
* Kiểm tra địa chỉ API được cấu hình trong frontend.

### Không thể thêm hoặc cập nhật dữ liệu

* Kiểm tra các trường bắt buộc.
* Kiểm tra định dạng dữ liệu.
* Kiểm tra mã định danh có bị trùng hay không.
* Đọc thông báo lỗi được hệ thống trả về.

### Không thể lập phiếu mượn

* Kiểm tra thông tin độc giả.
* Kiểm tra sách được chọn.
* Kiểm tra số lượng yêu cầu và các điều kiện mượn sách.

### Không thể trả sách

* Kiểm tra mã phiếu mượn.
* Kiểm tra trạng thái hiện tại của phiếu.
* Kiểm tra thông tin số lượng trả, hỏng hoặc mất.

## 10. Lưu ý khi sử dụng

* Kiểm tra thông tin trước khi xác nhận các thao tác làm thay đổi dữ liệu.
* Không tạo nhiều phiếu mượn cho cùng một nghiệp vụ ngoài ý muốn.
* Kiểm tra trạng thái phiếu sau khi hoàn thành trả sách.
* Không chia sẻ thông tin tài khoản hoặc dữ liệu nhạy cảm.
* Khi gặp lỗi, ghi nhận thông báo lỗi và cung cấp thông tin cần thiết cho người quản trị hệ thống.

## 11. Phạm vi tài liệu

Tài liệu này mô tả quy trình sử dụng ở mức tổng quát. Các bước cụ thể cần được điều chỉnh theo giao diện và chức năng thực tế của phiên bản hệ thống đang triển khai.
