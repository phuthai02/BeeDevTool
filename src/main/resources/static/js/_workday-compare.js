function WorkdayCompareController($scope, $http, $document) {
    $scope.systemFiles = [];
    $scope.compareFiles = [];

    $scope.triggerSystemFileInput = function () {
        document.getElementById('systemFileInput').click();
        setLoading(true);
    };

    $scope.triggerCompareFileInput = function () {
        document.getElementById('compareFileInput').click();
        setLoading(true);
    };

    $scope.handleSystemFile = function (files) {
        let filesHandle = Array.from(files);
        let newFiles = filesHandle.filter(file => {
            return !$scope.systemFiles.some(selectedFile => selectedFile.name === file.name && selectedFile.size === file.size);
        });
        $scope.systemFiles = $scope.systemFiles.concat(newFiles);
        $scope.$apply();
        setLoading(false);
    };

    $scope.handleCompareFile = function (files) {
        let filesHandle = Array.from(files);
        let newFiles = filesHandle.filter(file => {
            return !$scope.compareFiles.some(selectedFile => selectedFile.name === file.name && selectedFile.size === file.size);
        });
        $scope.compareFiles = $scope.compareFiles.concat(newFiles);
        $scope.$apply();
        setLoading(false);
    };

    $scope.removeAllFiles = function (type) {
        if (type === 'system') {
            $scope.systemFiles = [];
        } else if (type === 'compare') {
            $scope.compareFiles = [];
        }
    };

    $scope.removeFile = function (type, index) {
        if (type === 'system') {
            $scope.systemFiles.splice(index, 1);
        } else if (type === 'compare') {
            $scope.compareFiles.splice(index, 1);
        }
    };

    $scope.start = function () {
        if ($scope.systemFiles.length === 0 || $scope.compareFiles.length === 0) {
            Toast.fire({
                icon: 'warning', title: "Vui lòng tải cả file hệ thống và file đối chiếu lên để bắt đầu!"
            });
            setLoading(false);
            return;
        }

        setLoading(true);

        let formData = new FormData();

        $scope.systemFiles.forEach(function (file) {
            formData.append('systemFiles', file);
        });

        $scope.compareFiles.forEach(function (file) {
            formData.append('compareFiles', file);
        });

        $http.post('/workday-compare/compare', formData, {
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            transformResponse: [function (data) {
                return JSON.parse(data);
            }]
        }).then(function (response) {
            console.log(response.data);
            if (response.data.code == 1) {

                Toast.fire({
                    icon: 'success', title: response.data.message
                });
            } else {
                Toast.fire({
                    icon: 'error', title: response.data.message
                });
            }
        }).catch(function (error) {
            Toast.fire({
                icon: 'error', title: 'Lỗi hệ thống vui lòng liên hệ nhà phát triển'
            });
        }).finally(function () {
            setLoading(false);
        });
    };
}
