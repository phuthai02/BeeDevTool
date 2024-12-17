function WorkdaySummaryController($scope, $http, $document) {
    $scope.selectedShift = '1';
    $scope.modes = [
        {value: '1', label: 'Ca ngày'},
        {value: '2', label: 'Ca đêm'}
    ];
    $scope.selectedFiles = [];
    $scope.workdaySummarys = [];

    $scope.triggerFileInput = function () {
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




}
