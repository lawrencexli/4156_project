'use strict'
let uploaded_img;
let id = '';

$(function () {
    $('#img_upload_btn').click(async function () {
        if (id == '') {
            await generate_id();
            upload_img();
        } else {
            upload_img();
        }
    });
});

$(function () {
    $('#img_input').change(function () {
        let fileName = this.files[0].name;
        // let fileSize = this.files[0].size;
        uploaded_img = this.files[0];
        $("#uploaded_img").empty();
        $("#uploaded_img").append(fileName + '<br>')
        // alert('FileName : ' + fileName + '\nFileSize : ' + fileSize + ' bytes');
    });
});

$(function () {
    $('#img_crop_form').submit(function () {
        let x = $("#img_x").val();
        let y = $("#img_y").val();
    });
});


$(function () {
    $('#img_tran_form').submit(function () {
        let values = $(this).serialize();
        alert(values);
    });
});

$(function () {
    $('#img_satu_form').submit(function () {
        let values = $(this).serialize();
        alert(values);
    });
});

async function generate_id() {
    const response = await fetch("http://localhost:8080/api/generate");
    const data = await response.json();
    console.log(data);
    id = data.responseMessage;
}

async function upload_img() {
    let formData = new FormData();
    formData.append("id", id);
    formData.append("file", uploaded_img);

    fetch("http://localhost:8080/api/upload", {
        method: "POST",
        body: formData,
    })
        .then((response) => response.json())
        .then((json) => console.log(json));
}