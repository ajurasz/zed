function shellCommand(http, command, success, error) {
    http.get('http://localhost:8080/jolokia/exec/zed:name=zedShell/invokeCommand/' + command).
        success(function (data, status, headers, config) {
            success(data);
        }).
        error(function (data, status, headers, config) {
            error();
        });
}