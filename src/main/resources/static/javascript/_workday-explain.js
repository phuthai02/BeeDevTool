function WorkdayExplainController($scope, $http, $timeout) {
    $scope.selectedFiles = [];
    $scope.workdayExplains = [];

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

    $scope.isLate = function (timeIn) {
        const timeInDate = new Date(`2002-01-01T${timeIn}:00`);
        const eightAM = new Date('2002-01-01T08:00:00');
        return timeInDate > eightAM;
    };

    $scope.isEarly = function (timeOut, dateStr) {
        const [day, month, year] = dateStr.split('/');
        const date = new Date(`${year}-${month}-${day}`);
        const cutoffTime = date.getDay() === 6 ? '12:00' : '17:30';
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
        return "(T" + (moment(explainDay, 'DD/MM/YYYY').day() + 1) + ") " + explainDay;
    };

    $scope.start = function () {
        if ($scope.selectedFiles.length === 0) {
            Toast.fire({
                icon: 'warning', title: "Vui lòng tải tệp lên để bắt đầu!"
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
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            transformResponse: [function (data) {
                return JSON.parse(data);
            }]
        }).then(function (response) {
            console.log(response.data);
            if (response.data.code == 1) {
                $scope.workdayExplains = response.data.data;
                $scope.workdayExplains.forEach(function (item) {
                    item.explainDay = $scope.formatDate(item.explainDay);
                    item.staffCode = +item.staffCode;
                    item.timeIn = item.timeIn.padStart(5, '0');
                    item.timeOut = item.timeOut.padStart(5, '0');

                    // var timeInDate = new Date('2000-01-01T' + item.timeIn + ':00'),
                    //     timeOutDate = new Date('2000-01-01T' + item.timeOut + ':00'),
                    //     timeThreshold = new Date('2000-01-01T08:00:00'),
                    //     endThreshold = new Date('2000-01-01T17:30:00'),
                    //     saturdayThreshold = new Date('2000-01-01T12:00:00');
                    //
                    // item.timeIn = timeInDate < timeThreshold ? '08:00' : item.timeIn;
                    // var explainDay = new Date(item.explainDay);
                    // console.log(explainDay.getDay())
                    // if (explainDay.getDay() === 6) {
                    //     if (timeOutDate > saturdayThreshold) {
                    //         item.timeOut = '12:00';
                    //     }
                    // } else if (timeOutDate > endThreshold) {
                    //     item.timeOut = '17:30';
                    // }
                });
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
};
