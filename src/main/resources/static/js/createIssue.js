
function searchUser(name) {
    //    alert(name)
    fetch("/getUser?name=" + name, {
        method: "GET"
    })
        .then(res => res.json())
        .then(data => {
            console.log(data)
        })
        .catch(err => {
            console.log(err)
        })
}

var title = document.getElementById("issueTitle")
var reason = document.getElementById("issueReason")
var status = document.getElementsByClassName("issueStatusSelect")[0]

function checkFields(userId) {
    if (title.value.length <= 0 || reason.value.length <= 0) {
        alert("Fill all fields")
        return
    }
    createIssue(userId)
}

function showSelectedSubCategories(e, category) {
    console.log(e.target)
    if (this.selected) {
        alert(category)
    }
}

function updateIssue(issueId) {
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
        .then(res => res.json())
        .then(data => {
            console.log(data)
        })
        .catch(err => {
            console.log(err)
        })
}

function createIssue(userId) {
    var mBody = {
        title: title.value,
        reason: reason.value,
        categoryId: document.getElementsByClassName("issueCatSelect")[0].value,
        subCategoryId: document.getElementsByClassName("issueSubCatSelect")[0].value,
        status: "PENDING",
        userId: userId,
        solution: "",
        solverId: document.getElementsByClassName("issueSolveSelect")[0].value,
    }
    fetch("/createIssue", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(mBody)
    })
        .then(res => res.json())
        .then(data => {
            console.log(data)
        })
        .catch(err => {
            console.log(err)
        })

}

function getSubCategories(catId) {
    fetch("/subCategories?catId=" + catId)
        .then(res => res.json())
        .then(data => {
            console.log(data)
            document.getElementById("subHolder").innerHTML = ""
            document.getElementById("subHolder").appendChild(subCategoryBabies(data))
        })
        .catch(err => {
            console.log(err)
        })
}

getSubCategories(1)

function subCategoryBabies(data) {
    var card = document.createElement('select')
    card.classList.add = "form-select"
    card.classList.add = "mb-3"
    card.classList.add = "subCatSelect"
    card.setAttribute("class","form-select mb-3 issueSubCatSelect")
    card.setAttribute("aria-label" ,".form-select example")

    data.forEach(item => {
        var opt = document.createElement('option')
        opt.value = item.id
        opt.innerHTML = item.catName
        card.appendChild(opt)
    })

    return card
}

try {
    document.getElementsByClassName("issueCatSelect")[0].addEventListener('input', function (e) {
        getSubCategories(e.target.value)
    })
} catch (e) {
    console.log(e)
}