
function searchUser(name){
//    alert(name)
    fetch("/getUser?name=" + name, {
        method: "GET"
    })
    .then(res=>res.json())
    .then(data=>{
        console.log(data)
    })
    .catch(err=>{
        console.log(err)
    })
}

var title = document.getElementById("issueTitle")
var reason = document.getElementById("issueReason")

function checkFields(userId){
    if(title.value.length <=0 || reason.value.length <=0){
        alert("Fill all fields")
        return
    }
    createIssue(userId)
}

$( function() {
    var availableTags = [
      "ActionScript",
      "AppleScript",
      "Asp",
      "BASIC",
      "C",
      "C++",
      "Clojure",
      "COBOL",
      "ColdFusion",
      "Erlang",
      "Fortran",
      "Groovy",
      "Haskell",
      "Java",
      "JavaScript",
      "Lisp",
      "Perl",
      "PHP",
      "Python",
      "Ruby",
      "Scala",
      "Scheme"
    ];
    $( "#tags" ).autocomplete({
      source: availableTags
    });
  } );

  document.getElementById("tags").addEventListener("input", function(e){
    e.target.value
  })

function showSelectedSubCategories(e, category){
    console.log(e.target)
    if(this.selected){
        alert(category)
    }
}

function createIssue(userId){
    var mBody = {
        title: title.value,
        reason: reason.value,
        categoryId: 1,
        subCategoryId: 1,
        status: "PENDING",
        userId: userId,
        solverId: 2,
    }
    fetch("/createIssue", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(mBody)
    })
    .then(res=>res.json())
    .then(data=>{
        console.log(data)
    })
    .catch(err=>{
        console.log(err)
    })

}