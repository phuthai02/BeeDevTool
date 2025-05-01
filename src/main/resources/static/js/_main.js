const Toast = Swal.mixin({
    toast: true,
    showConfirmButton: false,
    position: "top-end",
    timer: 5000,
    timerProgressBar: true,
    didOpen: (toast) => {
        toast.addEventListener('mouseenter', Swal.stopTimer)
        toast.addEventListener('mouseleave', Swal.resumeTimer)
    }
});

document.addEventListener('dragover', function(e) {
    e.preventDefault();
    e.stopPropagation();
}, false);

document.addEventListener('drop', function(e) {
    e.preventDefault();
    e.stopPropagation();
}, false);

const select = (el, all = false) => {
    el = el.trim()
    if (all) {
        return [...document.querySelectorAll(el)]
    } else {
        return document.querySelector(el)
    }
}

const on = (type, el, listener, all = false) => {
    if (all) {
        select(el, all).forEach(e => e.addEventListener(type, listener))
    } else {
        select(el, all).addEventListener(type, listener)
    }
}

if (select('.toggle-sidebar-btn')) {
    on('click', '.toggle-sidebar-btn', function (e) {
        select('body').classList.toggle('toggle-sidebar')
    })
}

const setLoading = (isLoading) => document.getElementById('loading-screen').style.visibility = isLoading ? 'visible' : 'hidden';

document.getElementById('cancel-btn').addEventListener('click', function () {
    setLoading(false);
});
