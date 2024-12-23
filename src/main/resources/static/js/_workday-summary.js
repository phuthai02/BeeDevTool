function WorkdaySummaryController($scope, $http, $document) {
    $scope.selectedFiles = [];
    $scope.workdaySummarys = [];

    $scope.selectedShift = '1';
    $scope.shifts = [
        {value: '1', label: 'Ca ngày'},
        {value: '2', label: 'Ca đêm'}
    ];

    $scope.selectedMonth = moment().subtract(1, 'months').format('MM');
    $scope.months = [
        {value: '01', label: 'Tháng 1'},
        {value: '02', label: 'Tháng 2'},
        {value: '03', label: 'Tháng 3'},
        {value: '04', label: 'Tháng 4'},
        {value: '05', label: 'Tháng 5'},
        {value: '06', label: 'Tháng 6'},
        {value: '07', label: 'Tháng 7'},
        {value: '08', label: 'Tháng 8'},
        {value: '09', label: 'Tháng 9'},
        {value: '10', label: 'Tháng 10'},
        {value: '11', label: 'Tháng 11'},
        {value: '12', label: 'Tháng 12'}
    ];

    $scope.selectedYear = moment().format('YYYY');
    $scope.years = [
        {value: '2024', label: 'Năm 2024'},
        {value: '2025', label: 'Năm 2025'},
        {value: '2026', label: 'Năm 2026'},
        {value: '2027', label: 'Năm 2027'},
        {value: '2028', label: 'Năm 2028'},
        {value: '2029', label: 'Năm 2029'},
        {value: '2030', label: 'Năm 2030'},
    ];

    $scope.daysInMonth = moment($scope.selectedYear + '-' + $scope.selectedMonth, 'YYYY-MM').daysInMonth();;

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

    $scope.start = function () {
        if ($scope.selectedFiles.length === 0) {
            Toast.fire({
                icon: 'error', title: "Vui lòng tải file lên để bắt đầu!"
            });
            return;
        }

        setLoading(true);

        let formData = new FormData();

        $scope.selectedFiles.forEach(function (file) {
            formData.append('files', file);
        });

        $http.post('/workday-summary/upload', formData, {
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            transformResponse: [function (data) {
                return JSON.parse(data);
            }]
        }).then(function (response) {
            console.log(response.data);
            if (response.data.code == 1) {

            } else {
                Toast.fire({
                    icon: 'error', title: response.data.message
                });
            }
        }).catch(function (error) {
            console.log(error);
            Toast.fire({
                icon: 'error', title: 'Lỗi hệ thống vui lòng liên hệ nhà phát triển'
            });
        }).finally(function () {
            setLoading(false);
        });
    };

}
