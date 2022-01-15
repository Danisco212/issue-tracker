function logoutUser(){
    document.cookie = "Issue_AuthToken=;path=/";
    var getUrl = window.location;
    var baseUrl = getUrl.protocol + "//" + getUrl.host;
    window.open(baseUrl + "/login", "_self")
}

try{
            $(document).ready(function() {
                $('#example').DataTable();
            } );
}catch (e){
console.log(e)
}