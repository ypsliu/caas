module.exports = function(grunt) {
    grunt.initConfig({
        clean: {
            adminJs: {
                src: ["admin/js"]
            },
            userJs: {
                src: ["user/js"]
            }
        },
        uglify: {
            adminJs: {
                files: [{
                    expand: true,
                    cwd: 'admin/js',
                    src: '**/*.js',
                    dest: 'admin/jsmin'
                }]
            },
            userJs: {
                files: [{
                    expand: true,
                    cwd: 'user/js',
                    src: '**/*.js',
                    dest: 'user/jsmin'
                }]
            }
        },
        copy: {
            tpl: {
                expand: true,
                cwd: 'admin/js',
                src: '**/*.html',
                dest: 'admin/jsmin/',
            }
        },
        rename: {
            backupAdminJs: {
                files: [{
                    src: ['admin/js'], 
                    dest: 'admin/jsource'
                }]
            },
            switchToAdminMinJs: {
                files: [{
                    src: ['admin/jsmin'], 
                    dest: 'admin/js'
                }]
            },
            recoverAdminJs: {
                files: [{
                    src: ['admin/jsource'], 
                    dest: 'admin/js'
                }]
            },
            backupUserJs: {
                files: [{
                    src: ['user/js'], 
                    dest: 'user/jsource'
                }]
            },
            switchToUserMinJs: {
                files: [{
                    src: ['user/jsmin'], 
                    dest: 'user/js'
                }]
            },
            recoverUserJs: {
                files: [{
                    src: ['user/jsource'], 
                    dest: 'user/js'
                }]
            }
        }
    });

    grunt.loadNpmTasks("grunt-contrib-copy");
    grunt.loadNpmTasks("grunt-contrib-clean");
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-rename');
    grunt.loadNpmTasks('grunt-spritefiles');

    grunt.registerTask("recoverAdminJs", ["clean:adminJs", "rename:recoverAdminJs"]);
    grunt.registerTask("minifyAdminJs", ["uglify:adminJs", "copy:tpl", "rename:backupAdminJs", "rename:switchToAdminMinJs"]);

    grunt.registerTask("recoverUserJs", ["clean:userJs", "rename:recoverUserJs"]);
    grunt.registerTask("minifyUserJs", ["uglify:userJs", "rename:backupUserJs", "rename:switchToUserMinJs"]);
};