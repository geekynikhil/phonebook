var contactsApp = angular.module('contactsApp', ['ngRoute', 'ui.bootstrap'])

contactsApp.config(['$routeProvider',
    function ($routeProvider) {

        $routeProvider
            .when('/', {
                templateUrl: 'templates/contacts.html',
                controller: 'ContactsController'
            })
            .when('/groups', {
                templateUrl: 'templates/groups.html',
                controller: 'GroupsController'
            })
            .when('/groups/new', {
                templateUrl: 'templates/create-group.html',
                controller: 'NewGroupController'
            })
            .otherwise('/')
    }
])
