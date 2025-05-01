function WorkdayStandardizeController($scope, $http, $document) {
    $scope.selectedFiles = [];
    $scope.workdayStandardize = [];
    $scope.isDragging = false; // Thêm biến theo dõi trạng thái kéo thả

    // Khởi tạo directive cho sự kiện kéo thả
    $scope.initDragDrop = function() {
        // Tìm dropzone element sau khi DOM đã được render
        var dropZone = document.querySelector('.file-dropzone');

        if (dropZone) {
            // Gắn các sự kiện kéo thả
            dropZone.addEventListener('dragover', $scope.handleDragOver);
            dropZone.addEventListener('dragleave', $scope.handleDragLeave);
            dropZone.addEventListener('drop', $scope.handleDrop);
        }
    };

    // Gọi hàm khởi tạo sau khi DOM đã tải xong
    angular.element(document).ready(function() {
        $scope.initDragDrop();
    });

    $scope.triggerFileInput = function () {
        if ($scope.selectedFiles.length > 0) {
            Toast.fire({
                icon: 'warning',
                title: "Chỉ được chọn 1 file duy nhất"
            });
            return
        }

        setLoading(true);

        let input = document.createElement('input');
        input.type = 'file';
        input.multiple = true;
        input.accept = '.xls, .xlsx, .xlsm, .csv';

        input.onchange = function (event) {
            $scope.$apply(function () {
                let files = Array.from(event.target.files);
                let newFiles = files.filter(file => {
                    return !$scope.selectedFiles.some(selectedFile => selectedFile.name === file.name && selectedFile.size === file.size);
                });
                $scope.selectedFiles = $scope.selectedFiles.concat(newFiles);
                setLoading(false);
            });
        };

        input.click();
    };

    // Xử lý sự kiện kéo thả file
    $scope.handleDragOver = function(event) {
        event.preventDefault();
        event.stopPropagation();
        if (!$scope.isDragging) {
            $scope.isDragging = true;
            $scope.$apply();
        }
    };

    $scope.handleDragLeave = function(event) {
        event.preventDefault();
        event.stopPropagation();

        // Kiểm tra xem sự kiện dragleave có thực sự ra khỏi vùng drop hay không
        var rect = event.target.getBoundingClientRect();
        var isLeaving = event.clientX < rect.left ||
            event.clientX >= rect.right ||
            event.clientY < rect.top ||
            event.clientY >= rect.bottom;

        if (isLeaving) {
            $scope.isDragging = false;
            $scope.$apply();
        }
    };

    $scope.handleDrop = function(event) {
        event.preventDefault();
        event.stopPropagation();
        $scope.isDragging = false;

        if ($scope.selectedFiles.length > 0) {
            Toast.fire({
                icon: 'warning',
                title: "Chỉ được chọn 1 file duy nhất"
            });
            $scope.$apply();
            return;
        }

        setLoading(true);

        let files = Array.from(event.dataTransfer.files);

        // Lọc ra các file có định dạng hợp lệ
        let validFiles = files.filter(file => {
            const extension = file.name.split('.').pop().toLowerCase();
            return ['xls', 'xlsx', 'xlsm', 'csv'].includes(extension);
        });

        if (validFiles.length !== files.length) {
            Toast.fire({
                icon: 'warning',
                title: "Có file không đúng định dạng (.xls, .xlsx, .xlsm, .csv)"
            });
        }

        if (validFiles.length > 0) {
            $scope.$apply(function() {
                let newFiles = validFiles.filter(file => {
                    return !$scope.selectedFiles.some(selectedFile =>
                        selectedFile.name === file.name && selectedFile.size === file.size);
                });
                $scope.selectedFiles = $scope.selectedFiles.concat(newFiles);
            });
        }

        setLoading(false);
        $scope.$apply();
    };

    $scope.removeFile = function (index) {
        $scope.selectedFiles.splice(index, 1);
    };

    $scope.removeAllFile = function () {
        $scope.selectedFiles = [];
    };

    $scope.start = function () {
        if ($scope.selectedFiles.length === 0) {
            Toast.fire({
                icon: 'warning',
                title: "Vui lòng tải file lên để bắt đầu"
            });
            return;
        }

        setLoading(true);

        let formData = new FormData();

        $scope.selectedFiles.forEach(function (file) {
            formData.append('files', file);
        });

        $http.post('/workday-standardize/handle', formData, {
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            transformResponse: [function (data) {
                return JSON.parse(data);
            }]
        }).then(function (response) {
            if (response.data.code == 1) {
                const byteCharacters = atob(response.data.data);
                const byteArray = new Uint8Array(byteCharacters.length);
                for (let i = 0; i < byteCharacters.length; i++) {
                    byteArray[i] = byteCharacters.charCodeAt(i);
                }
                const blob = new Blob([byteArray], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
                const link = document.createElement('a');
                link.href = URL.createObjectURL(blob);
                const now = new Date();
                const hh = String(now.getHours()).padStart(2, '0');
                const mm = String(now.getMinutes()).padStart(2, '0');
                const ss = String(now.getSeconds()).padStart(2, '0');
                const filename = `chuan_hoa_cong_${hh}_${mm}_${ss}.xlsx`;
                link.download = filename;
                link.click();
            } else {
                Toast.fire({
                    icon: 'error',
                    title: response.data.message || 'Lỗi xử lý dữ liệu'
                });
            }
        }).catch(function (error) {
            console.log(error);
            Toast.fire({
                icon: 'error',
                title: 'Lỗi hệ thống vui lòng liên hệ nhà phát triển'
            });
        }).finally(function () {
            setLoading(false);
        });
    };

    // Sự kiện khi controller bị hủy để dọn dẹp các sự kiện đã đăng ký
    $scope.$on('$destroy', function() {
        var dropZone = document.querySelector('.file-dropzone');
        if (dropZone) {
            dropZone.removeEventListener('dragover', $scope.handleDragOver);
            dropZone.removeEventListener('dragleave', $scope.handleDragLeave);
            dropZone.removeEventListener('drop', $scope.handleDrop);
        }
    });
}