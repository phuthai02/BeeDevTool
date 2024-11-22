function WorkdayExplainController($scope, $http, $document) {
    $scope.selectedFiles = [];
    $scope.workdayExplains = [];
    $scope.selectedMonth = moment().format('MM');

    $scope.removeFile = function (index) {
        $scope.selectedFiles.splice(index, 1);
    };

    $scope.removeAllFile = function () {
        $scope.selectedFiles = [];
    };

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

    $scope.isLate = function (timeIn) {
        let timeInDate = new Date(`2002-01-01T${timeIn}:00`);
        let eightAM = new Date('2002-01-01T08:00:00');
        return timeInDate > eightAM;
    };

    $scope.isEarly = function (timeOut, dateStr) {
        let [day, month, year] = dateStr.split('/');
        let date = new Date(`${year}-${month}-${day}`);
        let cutoffTime = date.getDay() === 6 ? '12:00' : '17:30';
        return new Date(`2002-01-01T${timeOut}:00`) < new Date(`2002-01-01T${cutoffTime}:00`);
    };

    $scope.editValue = function (item, field) {
        $scope.saveCurrentEditingField();
        item.isEditing = item.isEditing || {};
        item.isEditing[field] = true;
    };

    $scope.saveValueIfEditing = function (item, field) {
        if (item.isEditing && item.isEditing[field]) {
            $scope.saveValue(item, field);
        }
    };

    $scope.saveValue = function (item, field) {
        item.isEditing[field] = false;
        if (field === 'timeIn' || field === 'timeOut') {
            item[field] = item[field].padStart(5, '0');
            $scope.updateTimeCount();
        }
    };

    $scope.saveCurrentEditingField = function () {
        angular.forEach($scope.workdayExplains, function (item) {
            angular.forEach(item.isEditing, function (isEditing, field) {
                if (isEditing) {
                    $scope.saveValue(item, field);
                }
            });
        });
    };

    $scope.formatDate = function (dateStr) {
        return moment(dateStr, 'ddd MMM DD HH:mm:ss [ICT] YYYY').format('DD/MM/YYYY');
    };

    $scope.getDay = function (explainDay) {
        let dayOfWeek = moment(explainDay, 'DD/MM/YYYY').day();
        let dayText = dayOfWeek === 0 ? "CN" : "T" + (dayOfWeek + 1);
        return "(" + dayText + ") " + explainDay;
    };

    $scope.setupTable = function () {
        $scope.workdayExplains.forEach(function (item) {
            item.explainDay = $scope.formatDate(item.explainDay);
            item.staffCode = +item.staffCode;
            item.timeIn = item.timeIn.padStart(5, '0');
            item.timeOut = item.timeOut.padStart(5, '0');

            let timeInDate = new Date('2002-01-01T' + item.timeIn + ':00'),
                timeOutDate = new Date('2002-01-01T' + item.timeOut + ':00'),
                startThreshold = new Date('2002-01-01T08:00:00'), saturdayThreshold = new Date('2002-01-01T12:00:00'),
                endThreshold = new Date('2002-01-01T17:30:00'), dayOfWeek = moment(item.explainDay, 'DD/MM/YYYY').day();

            item.timeIn = timeInDate < startThreshold ? '08:00' : item.timeIn;

            item.timeOut = dayOfWeek === 6 ? (timeOutDate > saturdayThreshold ? '12:00' : item.timeOut) : (timeOutDate > endThreshold ? '17:30' : item.timeOut);

            $scope.updateTimeCount();
        });
    };

    $scope.updateTimeCount = function () {
        $scope.workdayExplains.forEach(function (item) {
            let timeInDate = new Date('2002-01-01T' + item.timeIn + ':00'),
                timeOutDate = new Date('2002-01-01T' + item.timeOut + ':00'),
                startThreshold = new Date('2002-01-01T08:00:00'), saturdayThreshold = new Date('2002-01-01T12:00:00'),
                endThreshold = new Date('2002-01-01T17:30:00'), dayOfWeek = moment(item.explainDay, 'DD/MM/YYYY').day();

            if (dayOfWeek === 6 && timeOutDate >= saturdayThreshold) {
                item.timeCount = 4;
            } else if (timeInDate <= startThreshold && timeOutDate >= endThreshold) {
                item.timeCount = 8;
            } else {
                let timeInMinutes = timeInDate.getHours() * 60 + timeInDate.getMinutes();
                let timeOutMinutes = timeOutDate.getHours() * 60 + timeOutDate.getMinutes();
                let totalMinutes = timeOutMinutes - timeInMinutes;
                if (totalMinutes > 240) {
                    totalMinutes -= 90;
                }
                let totalHours = totalMinutes / 60;
                item.timeCount = (totalHours % 1 === 0) ? totalHours : totalHours.toFixed(2);
            }
        });
    };

    $document.on('keydown', function (event) {
        if (event.key === 't' || event.key === 'T') {
            // if ($scope.workdayExplains.length == 0) return;
            $scope.$apply(function () {
                $('#editModal').modal('show');
            });
        }
    });

    $scope.start = function () {
        if ($scope.selectedFiles.length === 0) {
            Toast.fire({
                icon: 'warning', title: "Vui lòng tải file lên để bắt đầu!"
            });
            setLoading(false);
            return;
        }

        setLoading(true);

        let formData = new FormData();

        $scope.selectedFiles.forEach(function (file) {
            formData.append('files', file);
        });

        $http.post('/workday-explain/upload', formData, {
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            transformResponse: [function (data) {
                return JSON.parse(data);
            }]
        }).then(function (response) {
            console.log(response.data);
            if (response.data.code == 1) {
                $scope.workdayExplains = response.data.data;
                $scope.setupTable();
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

    $scope.export = function () {
        if ($scope.workdayExplains.length === 0) {
            Toast.fire({
                icon: 'warning', title: "Không có dữ liệu để xuất file!"
            });
            return;
        }

        setLoading(true);

        $scope.workdayExplainsCopy = angular.copy($scope.workdayExplains);

        $scope.workdayExplainsCopy.forEach(function (item) {
            item.explainDay = moment(item.explainDay, 'DD/MM/YYYY').format('MM/DD/YYYY');
        });

        $http.post('/workday-explain/export', $scope.workdayExplainsCopy, {
            headers: {'Content-Type': 'application/json'},
            transformResponse: [function (data) {
                return JSON.parse(data);
            }]
        }).then(function (response) {
            console.log(response.data);
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
                const filename = `giai_trinh_cong_${hh}_${mm}_${ss}.xlsx`;
                link.download = filename;
                link.click();
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
};
