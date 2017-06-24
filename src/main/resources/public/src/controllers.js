/* Controllers */
angular
    .module(config.moduleName)
    .controller(
    'ContactsController',
    [
        '$rootScope', '$scope', '$http', '$uibModal',
        function ($rootScope, $scope, $http, $uibModal) {
            $rootScope.appInfo = {
                title: 'Contacts',
                pageClass: 'contacts'
            }
            function refreshContacts() {
                $http.get(config.apiRoutes.users)
                    .then(function (response) {
                        $scope.contacts = response.data
                    })
                    .catch(function (error) {
                        console.error(error)
                    })
            }
            refreshContacts()

            $scope.deleteContact = function (contactID) {
                $http.delete(config.apiRoutes.users + '/' + contactID)
                    .then(refreshContacts)
            }

            $scope.contactModal = function (mode, contact) {
                $scope.modalMode = mode
                $scope.contact = contact
                $scope.modalInstance = $uibModal.open({
                    controller: 'NewContactController',
                    templateUrl: 'templates/create-contact.html',
                    scope: $scope
                })
                    .result.then(function (result) {
                        if (result == 'success') {
                            refreshContacts()
                        }
                    })
                    .catch(function () { })
            }
        }
    ]
    )

    .controller(
    'NewContactController',
    [
        '$rootScope', '$scope', '$http', '$httpParamSerializerJQLike', '$controller',
        function ($rootScope, $scope, $http, $httpParamSerializerJQLike, $controller) {
            // Proxy to GroupsController to add groups manipulation functions in this scope
            $controller('GroupsController', {
                $rootScope: {},
                $scope: $scope
            })

            if ($scope.contact) {
                $scope.contact.groupId = []

                $scope.contact.groups.forEach(function (group) {
                    if (group) $scope.contact.groupId.push(group.id)
                })
            }

            $scope.addContact = function () {
                var promise
                if ($scope.modalMode == 'edit') {
                    promise = $http({
                        method: 'PUT',
                        url: config.apiRoutes.users + '/' + $scope.contact.id,
                        data: $httpParamSerializerJQLike($scope.contact),
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    })
                } else {
                    promise = $http({
                        method: 'POST',
                        url: config.apiRoutes.users,
                        data: $httpParamSerializerJQLike($scope.contact),
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    })
                }

                promise
                    .then(function success(response) {
                        $scope.$close('success')
                    })
                    .catch(function error(error) {
                        $scope.error = error.data && error.data.message
                    })
            }
        }
    ]
    )

    .controller(
    'GroupsController',
    [
        '$rootScope', '$scope', '$http', '$uibModal',
        function ($rootScope, $scope, $http, $uibModal) {
            $rootScope.appInfo = {
                title: 'Groups',
                pageClass: 'groups'
            }
            function refreshGroups() {
                $http.get(config.apiRoutes.groups)
                    .then(function success(response) {
                        $scope.groups = response.data
                    })
                    .catch(function error(error) {
                        console.error(error.data)
                    })
            }
            refreshGroups()

            $scope.deleteGroup = function (id) {
                $http.delete(config.apiRoutes.groups + '/' + id)
                    .finally(refreshGroups)
            }

            $scope.groupModal = function (mode, group) {
                $scope.group = group
                $scope.modalMode = mode
                $scope.modalInstance = $uibModal.open({
                    controller: 'NewGroupController',
                    templateUrl: 'templates/create-group.html',
                    scope: $scope
                })
                    .result.then(function (result) {
                        if (result == 'success') {
                            refreshGroups()
                        }
                    })
                    .catch(function () { })
            }
        }
    ]
    )

    .controller(
    'NewGroupController',
    [
        '$rootScope', '$scope', '$http', '$httpParamSerializerJQLike',
        function ($rootScope, $scope, $http, $httpParamSerializerJQLike) {
            $scope.createNewGroup = function () {
                var promise
                if ($scope.modalMode == 'edit') {
                    promise = $http({
                        method: 'PUT',
                        url: config.apiRoutes.groups + '/' + $scope.group.id,
                        data: $httpParamSerializerJQLike($scope.group),
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    })
                } else {
                    promise = $http({
                        method: 'POST',
                        url: config.apiRoutes.groups,
                        data: $httpParamSerializerJQLike($scope.group),
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    })
                }

                promise
                    .then(function success(response) {
                        $scope.$close('success')
                    })
                    .catch(function error(error) {
                        $scope.error = error.data && error.data.message
                    })
            }
        }
    ]
    )