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

    $scope.zoomIn = function () {
        $scope.zoomLevel += 0.05;
    };

    $scope.zoomOut = function () {
        if ($scope.zoomLevel > 0.05) {
            $scope.zoomLevel -= 0.05;
        }
    };

    $scope.getDaysInMonth = function () {
        $scope.daysInMonthArray = [];
        $scope.daysInMonth = moment($scope.selectedYear + '-' + $scope.selectedMonth, 'YYYY-MM').daysInMonth();
        for (var i = 1; i <= $scope.daysInMonth; i++) {
            var day = moment($scope.selectedYear + '-' + $scope.selectedMonth + '-' + i, 'YYYY-MM-DD');
            let dayOfWeek = day.day();
            let dayText = dayOfWeek === 0 ? "CN" : "T" + (dayOfWeek + 1);
            $scope.daysInMonthArray.push(i + ' (' + dayText + ')');
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

    $scope.showToast = function (type, message) {
        Toast.fire({icon: type, title: message});
    };

    $scope.handleResponse = function (response, titleResponse, tableId) {
        if (response.data.code === 1) {
            $scope.titleResponse = titleResponse;
            $scope.visibleResponse = true;

            setTimeout(() => {
                const tbody = document.getElementById(tableId);
                const rhead = document.getElementById("header-staff");
                while (tbody.firstChild) {
                    tbody.removeChild(tbody.firstChild);
                }

                response.data.data.forEach((item, index) => {
                    $scope.renderTableRow(item, tbody, "HT", "staffSystem", index);
                    $scope.renderTableRow(item, tbody, "ĐC", "staffCompare", index);
                    tbody.appendChild(rhead.cloneNode(true));
                });
            }, 0);

            $scope.showToast('success', response.data.message);
        } else {
            $scope.showToast('error', response.data.message);
        }
    };

    $scope.renderTableRow = function (item, tbody, headerText, key, rowIndex) {
        if (!item[key]) return;
        const row = document.createElement("tr");
        const header = document.createElement("td");
        header.textContent = (!item['staffSystem'] || !item['staffCompare']) ? rowIndex + 1: headerText;
        row.appendChild(header);
        item[key].forEach((value, index) => {
            const cell = document.createElement("td");
            if (index === 0 || index === item[key].length - 1) value = Number(value);
            cell.textContent = (index !== item[key].length - 1 && index !== 1) ? value.toString().toUpperCase() : value;
            row.appendChild(cell);
        });
        tbody.appendChild(row);
    };

    $scope.process = function (url, formData, titleResponse, tableId) {
        formData.append('daysInMonth', $scope.daysInMonth);
        $http.post(url, formData, {
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            transformResponse: [data => JSON.parse(data)]
        }).then(response => {
            $scope.handleResponse(response, titleResponse, tableId);
        }).catch(error => {
            console.error(error);
            $scope.showToast('error', 'Lỗi hệ thống vui lòng liên hệ nhà phát triển');
        }).finally(() => {
            setLoading(false);
        });
    };

    $scope.reviewSystem = function () {
        if ($scope.systemFiles.length === 0) {
            $scope.showToast('warning', "Vui lòng tải file hệ thống lên để bắt đầu!");
            return;
        }
        setLoading(true);
        const formData = new FormData();
        $scope.systemFiles.forEach(file => formData.append('systemFiles', file));
        $scope.process('/workday-compare/review/system', formData, "DỮ LIỆU HỆ THỐNG", "PT");
    };

    $scope.reviewCompare = function () {
        if ($scope.compareFiles.length === 0) {
            $scope.showToast('warning', "Vui lòng tải file đối chiếu lên để bắt đầu!");
            return;
        }
        setLoading(true);
        const formData = new FormData();
        $scope.compareFiles.forEach(file => formData.append('compareFiles', file));
        $scope.process('/workday-compare/review/compare', formData, "DỮ LIỆU ĐỐI CHIẾU", "PT");
    };

    $scope.start = function () {
        if ($scope.systemFiles.length === 0 || $scope.compareFiles.length === 0) {
            $scope.showToast('warning', "Vui lòng tải cả file hệ thống và file đối chiếu lên để bắt đầu!");
            return;
        }
        setLoading(true);
        const formData = new FormData();
        $scope.systemFiles.forEach(file => formData.append('systemFiles', file));
        $scope.compareFiles.forEach(file => formData.append('compareFiles', file));
        $scope.process('/workday-compare/compare', formData, "CÁC ĐỐI TƯỢNG ĐÁNG CHÚ Ý", "PT");
    };
}
