function WorkdayCompareController($scope, $http, $document) {
    $scope.systemFiles = [];
    $scope.compareFiles = [];
    $scope.titleResponse = "";
    $scope.visibleResponse = false;

    $scope.daysInMonthArray = [];
    $scope.daysInMonth = 0;
    $scope.selectedMonth = moment().subtract(1, 'months').format('MM');
    $scope.selectedYear = moment().format('YYYY');

    $scope.zoomLevel = 0.75;

    $scope.zoomIn = function() {
        $scope.zoomLevel += 0.05;
    };

    $scope.zoomOut = function() {
        if ($scope.zoomLevel > 0.05) {
            $scope.zoomLevel -= 0.05;
        }
    };

    $scope.getDaysInMonth = function () {
        $scope.daysInMonthArray = [];
        $scope.daysInMonth = moment($scope.selectedYear + '-' + $scope.selectedMonth, 'YYYY-MM').daysInMonth();
        for (var i = 1; i <= $scope.daysInMonth; i++) {
            $scope.daysInMonthArray.push(i);
        }
    };

    $scope.getDaysInMonth();

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

    $scope.years = [
        {value: '2024', label: 'Năm 2024'},
        {value: '2025', label: 'Năm 2025'},
        {value: '2026', label: 'Năm 2026'},
        {value: '2027', label: 'Năm 2027'},
        {value: '2028', label: 'Năm 2028'},
        {value: '2029', label: 'Năm 2029'},
        {value: '2030', label: 'Năm 2030'},
    ];

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

        formData.append('daysInMonth', $scope.daysInMonth);

        $http.post('/workday-compare/compare', formData, {
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            transformResponse: [function (data) {
                return JSON.parse(data);
            }]
        }).then(function (response) {
            if (response.data.code == 1) {
                $scope.titleResponse = "CÁC ĐỐI TƯỢNG ĐÁNG CHÚ Ý";
                $scope.visibleResponse = true;
                setTimeout(() => {
                    let tbody = document.getElementById("PT");
                    let rowHeader = document.getElementById("header-staff");

                    if (!rowHeader) {
                        console.error("Element with ID 'header-staff' not found.");
                        return;
                    }

                    response.data.data.forEach(item => {
                        const rowSystem = document.createElement("tr");
                        const headerSystem = document.createElement("td");
                        const rowCompare = document.createElement("tr");
                        const headerCompare = document.createElement("td");

                        headerSystem.textContent = "HT";
                        rowSystem.appendChild(headerSystem);
                        headerCompare.textContent = "ĐC";
                        rowCompare.appendChild(headerCompare);

                        item.staffSystem.forEach((system, index) => {
                            const cellSystem = document.createElement("td");
                            if (index === 0 || index === item.staffSystem.length - 1) system = Number(system);
                            cellSystem.textContent = (index !== item.staffSystem.length - 1 && index !== 1) ? system.toString().trim().toUpperCase() : system;
                            rowSystem.appendChild(cellSystem);
                        });

                        item.staffCompare.forEach((compare, index) => {
                            const cellCompare = document.createElement("td");
                            if (index === 0 || index === item.staffCompare.length - 1) compare = Number(compare);
                            cellCompare.textContent = (index !== item.staffCompare.length - 1 && index !== 1) ? compare.toString().trim().toUpperCase() : compare;
                            rowCompare.appendChild(cellCompare);
                        });

                        const clonedHeader = rowHeader.cloneNode(true);
                        tbody.appendChild(rowSystem);
                        tbody.appendChild(rowCompare);
                        tbody.appendChild(clonedHeader);
                    });
                }, 0);
                Toast.fire({
                    icon: 'success', title: response.data.message
                });
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
