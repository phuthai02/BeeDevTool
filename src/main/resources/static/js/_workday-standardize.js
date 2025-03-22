function WorkdayStandardizeController($scope, $http, $document) {
    $scope.selectedFiles = [];
    $scope.workdayStandardize = [];

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
}