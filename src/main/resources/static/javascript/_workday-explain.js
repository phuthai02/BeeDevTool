function WorkdayExplainController($scope, $http) {
    $scope.selectedFiles = [];

    $scope.removeFile = function (index) {
        $scope.selectedFiles.splice(index, 1);
    };

    $scope.removeAllFile = function () {
        $scope.selectedFiles = [];
    };

    $scope.triggerFileInput = function () {
        setLoading(true);

        const input = document.createElement('input');
        input.type = 'file';
        input.multiple = true;
        input.accept = '.xls, .xlsx, .xlsm, .csv';

        input.onchange = function (event) {
            $scope.$apply(function () {
                const files = Array.from(event.target.files);
                const newFiles = files.filter(file => {
                    return !$scope.selectedFiles.some(selectedFile => selectedFile.name === file.name && selectedFile.size === file.size);
                });
                $scope.selectedFiles = $scope.selectedFiles.concat(newFiles);
                setLoading(false);
            });
        };
        input.click();
    };

    $scope.start = function () {
        if ($scope.selectedFiles.length === 0) {
            Toast.fire({
                icon: 'warning',
                title: "Vui lòng tải tệp lên để bắt đầu!"
            });
            setLoading(false);
            return;
        }

        setLoading(true);

        const formData = new FormData();

        $scope.selectedFiles.forEach(function (file) {
            formData.append('files', file);
        });

        $http.post('/workday-explain/upload', formData, {
            headers: { 'Content-Type': undefined },
            transformRequest: angular.identity,
            transformResponse: [function (data) {
                return typeof data === 'string' ? { message: data } : data;
            }]
        }).then(function (response) {
            Toast.fire({
                icon: 'success',
                title: response.data.message
            });
        }).catch(function (error) {
            Toast.fire({
                icon: 'error',
                title: 'Lỗi hệ thống vui lòng liên hệ nhà phát triển'
            });
        }).finally(function () {
            setLoading(false);
        });
    };
};
