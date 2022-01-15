
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
var status = document.getElementsByClassName("issueStatusSelect")[0]

function checkFields(userId){
    if(title.value.length <=0 || reason.value.length <=0){
        alert("Fill all fields")
        return
    }
    createIssue(userId)
}

function showSelectedSubCategories(e, category){
    console.log(e.target)
    if(this.selected){
        alert(category)
    }
}

function updateIssue(issueId){
    var solution = document.getElementById("issueSolution")
    var mStatus = document.getElementsByClassName("issueStatusSelect")[0].value
    var mBody = {
            id: issueId,
            solution: solution.value,
            status: mStatus,
        }

       console.log(mBody)
        fetch("/updateIssue", {
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

function createIssue(userId){
    var mBody = {
        title: title.value,
        reason: reason.value,
        categoryId: 1,
        subCategoryId: 1,
        status: "PENDING",
        userId: userId,
        solution: "",
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