'use strict'
let img_list = [];
let id = '';
let formData = new FormData();

$(function () {
    $('#img_upload_btn').click(function () {
        if (id == '') {
            generate_id();
        }
        upload_img();
    });
});

$(function () {
    $('#img_input').change(function () {
        let fileName = this.files[0].name;
        let fileSize = this.files[0].size;
        formData.append("file", this.files[0], this.files[0].name);
        img_list.push(this.files[0]);
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

function generate_id() {
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/api/generate",
        dataType: "json",
        success: function (data, text) {
            console.log(data);
            id = data['responseMessage'];
            // alert(data);
        },
        error: function (error) {
            console.log(error);
        },
    });
}

function upload_img() {
    console.log(formData.getAll("file"));
    let data = {'id': id, 'img': img_list};
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/api/upload",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (response) {
            console.log(response);
        },
        error: function (response) {
            console.log(response);
        },
    });
}