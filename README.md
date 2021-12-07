# DUAN1
## FastFood - ỨNG DỤNG ĐẶT ĐỒ ĂN NHANH
### Dự án 1, lấy cảm hứng từ các ứng dụng đặt đồ ăn trên thị trường, chúng tôi đã xây dựng một ứng dụng đặt đồ ăn nhanh cho riêng mình
### Nền tảng triển khai
#### Database - Firebase và language Java
#### Quyền đăng nhập ứng dụng:
* Quyền admin - Toàn quyền sử dụng ứng dụng.
* Quyền chủ nhà hàng - Toàn quyền với nhà hàng của mình(Không được quyền thêm, xóa, sửa nhà hàng, không được xem doanh thu của nhà hàng khác, không được truy cập vô màn hình quản lí tài khoản)
* Quyền khách hàng - Được đặt món ăn, xem danh sách các nhà hàng, giỏ hàng và lịch sử mua hàng. Không được truy cập màn hình quản lí tài khoản và doanh thu.

## GIAO DIỆN DỨNG DỤNG
### Màn hình đăng nhập
* Màn hình đăng nhập có thể lưu lại mật khẩu tài khoản từ lần đăng nhập sau
* Bắt lỗi các trường hợp nhập liệu sai
<img src="https://user-images.githubusercontent.com/75264221/144962608-f10bfcce-43d4-400d-8513-383e00e90777.png" height="400" />

### Màn hình đăng kí
* Hình ảnh được tải lên được lưu trữ trên Firebase
* Bắt lỗi các trường hợp nhập liệu sai
* Sau khi nhập xong tất cả các thông tin, nhấn xác nhận và mã OTP sẽ được gửi đến số điện thoại đăng kí
<img src="https://user-images.githubusercontent.com/75264221/144962874-ca94b755-f1ab-4947-8607-4a5b466c8edc.png" height="400" />

### Màn hình chính
* RecycleView danh sách nhà hàng
* Người dùng có thể thả lưu trữ các danh sách nhà hàng mà mình yêu thích
* Nhà hàng được chia ra thành danh sách các loại để khách hàng dễ tìm kiếm
* Thêm, xóa, sửa thông tin loại nhà hàng
* Thêm, xóa, sửa thông tin nhà hàng
* Hiện thông tin tài khoản đăng nhập
* Tìm kiếm nhà hàng
https://user-images.githubusercontent.com/75264221/144964355-0f67fe8a-7ea6-4535-8431-40d47ab12ca3.mp4

### Màn hình món ăn - Màn hình đặt món ăn
* RecycleView danh sách món ăn trong nhà hàng
* Đánh giá nhà hàng
* Thêm, xóa sửa thông tin nhà hàng
* Tìm kiếm nhà hàng
* Thay đổi số lượng món ăn muốn đặt
* Thêm các món ăn phụ
https://user-images.githubusercontent.com/75264221/144964640-47e956d5-24a0-4b71-a013-8e348127898e.mp4

### Mằn hình giỏ hàng
* RecycleView danh sách giỏ hàng
* Xóa các món ăn trong giỏ hàng
* Thay đổi số lượng món ăn trong giỏ hàng
* Hiện tổng tiền khi chọn món ăn
https://user-images.githubusercontent.com/75264221/144964857-7d6eb0b6-0554-4c03-91a1-16d07b966f0a.mp4

### Màn hình thanh toán
Hiện thông tin địa chỉ, số điện thoại, tên người dùng từ dữ liệu lúc đăng nhập
RecycleView danh sách các món ăn người dùng đã chọn mua
Thông tin tiền phí vận chuyển, tổng tiền khi thanh toán. Bắt lỗi trường hợp tài khoản không đủ số dư

https://user-images.githubusercontent.com/75264221/144964953-bc41757c-5866-40ec-969e-ea81da8204da.mp4

### Màn hình quản lí tài khoản
* 2 tabLayout danh sách tài khoản chủ nhà hàng và khách hàng
* RecycleView danh sách tài khoản
* Danh sách người dùng cho phép xóa tài khoản 
https://user-images.githubusercontent.com/75264221/144965004-4ea6c94a-caca-466a-9eb8-e4bd6dff1d8f.mp4

### Màn hình cài đặt
<img src="https://user-images.githubusercontent.com/75264221/144965115-49e66b27-9a73-469a-a293-1a707eca6906.png" height="400" />


### Màn hình lịch sử mua hàng
* RecycleView danh sách các đơn hàng đã mua
https://user-images.githubusercontent.com/75264221/144965193-9e32b18d-5306-446e-a7b0-51b932713635.mp4

### Màn hình doanh thu nhà hàng
* BarChart biểu đồ doanh thu tất cả nhà hàng trong hệ thống, tính theo tháng. Lấy từ thư viện PhilJay:MPAndroidChart
* Đăng nhập với quyền chủ nhà hàng, Barchart chỉ hiện tổng doanh thu các nhà hàng của tài khoản đó
* RecyleView doanh thu tất cả nhà hàng được tính theo thời gian chọn
* Đăng nhập với quyền admin, có quyền xem doanh thu tất cả nhà hàng
* Đăng nhập với quyền chủ nhà hàng, chỉ có quyền xem thông tin doanh thu của nhà hàng mà tài khoản làm chủ
https://user-images.githubusercontent.com/75264221/144965308-9a16024a-f1c0-4ca4-b733-c1f75d33fd20.mp4

### Màn hình doanh thu món ăn
* Doanh thu các món ăn trong nhà hàng đã chọn(Doanh thu của món ăn được tính theo khoảng thời gian)
https://user-images.githubusercontent.com/75264221/144965379-166d1227-ccfd-4c70-8901-6832826200b0.mp4

