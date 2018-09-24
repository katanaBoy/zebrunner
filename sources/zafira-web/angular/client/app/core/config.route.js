(function () {
    'use strict';

    angular.module('app')
        .config(['$stateProvider', '$urlRouterProvider', '$httpProvider', '$ocLazyLoadProvider',
                function($stateProvider, $urlRouterProvider, $httpProvider, $ocLazyLoadProvider) {

                $stateProvider
	                .state('dashboard', {
	                    url: '/dashboards/:id',
	                    templateUrl: 'app/_dashboards/list.html'
	                })
	                .state('dashboards', {
	                    url: '/dashboards',
	                    templateUrl: 'app/_dashboards/list.html'
	                })
                    .state('views', {
                        url: '/views/:id',
                        templateUrl: 'app/_views/list.html'
                    })
                    //for testing pages 
                    .state('signin', {
                        url: '/signin',
                        templateUrl: 'app/page/signin.html'
                    })
                    .state('register', {
                        url: '/register',
                        templateUrl: 'app/page/register.html'
                    })
                    .state('signup', {
                        url: '/signup',
                        templateUrl: 'app/page/signup.html'
                    })
                    .state('forgotpwd', {
                        url: '/forgotpwd',
                        templateUrl: 'app/page/forgot-password.html'
                    })
                    .state('changepwd', {
                        url: '/changepwd',
                        templateUrl: 'app/page/change-password.html'
                    })
                    .state('forgotPassword', {
                        url: '/password/forgot',
                        templateUrl: 'app/_auth/forgot-password.html'
                    })
                    .state('resetPassword', {
                        url: '/password/reset',
                        templateUrl: 'app/_auth/reset-password.html'
                    })
                    .state('users/profile', {
                        url: '/users/profile',
                        templateUrl: 'app/_users/profile.html'
                    })
                    .state('users', {
                        url: '/users',
                        templateUrl: 'app/_users/list.html'
                    })
                    .state('tests/cases', {
                        url: '/tests/cases',
                        templateUrl: 'app/_testcases/list.html'
                    })
                    .state('tests/cases/metrics', {
                        url: '/tests/cases/:id/metrics',
                        templateUrl: 'app/_testcases/metrics/list.html'
                    })
                    .state('tests/run', {
	                    url: '/tests/runs/:id',
	                    templateUrl: 'app/_testruns/list.html'
	                })
                    .state('tests/runs', {
                        url: '/tests/runs',
                        templateUrl: 'app/_testruns/list.html'
                    })
                    .state('tenancies', {
                        url: '/tenancies',
                        templateUrl: 'app/_tenancies/list.html'
                    })
                    .state('tests/runs/info', {
                        url: '/tests/runs/:id/info/:testId',
                        templateUrl: 'app/_testruns/_info/list.html'
                    })
                    .state('settings', {
                        url: '/settings',
                        templateUrl: 'app/_settings/list.html'
                    })
                    .state('monitors', {
                        url: '/monitors',
                        templateUrl: 'app/_monitors/list.html'
                    })
                    .state('integrations', {
                        url: '/integrations',
                        templateUrl: 'app/_integrations/list.html'
                    })
                    .state('certifications', {
                        url: '/certification',
                        templateUrl: 'app/_certifications/list.html'
                    })
                    .state('404', {
                        url: '/404',
                        templateUrl: 'app/page/404.html'
                    })
                    .state('500', {
                        url: '/500',
                        templateUrl: 'app/page/500.html'
                    });

                $urlRouterProvider
                    .when('/', '/dashboards')
                    .when('', '/dashboards')
                    .otherwise('/404');

            }
        ]);
})();
