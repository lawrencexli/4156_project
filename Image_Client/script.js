"use strict";
let img_list = [];
let id = '';

$(function () {
    $('#img_upload_form').submit(function () {
        if (id){
            upload_img();
        } else {
            generate_id();
        }
    });
});

$(function () {
    $('#img_input').change(function(){
        let fileName = this.files[0].name;
        let fileSize = this.files[0].size;

        img_list.push(this.files[0]);
        $("#uploaded_img").append(fileName + '<br>')
        alert('FileName : ' + fileName + '\nFileSize : ' + fileSize + ' bytes');
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

function generate_id(){
    alert("test0");
    $.ajax({
        type: "GET",
        url: "localhost:8080/api/generate",
        dataType: "json",
        headers: {
            'Cache-Control': 'max-age=1000'
       },
        success: function (data, text) {
            alert("test1");
            // id = data['responseMessage'];
            // alert(data);
        },
        error: function (error) {
            alert("test2");
            alert(error);
        },
    });
}

function upload_img() {
    let data = { "genre": genre }
    $.ajax({
        type: "POST",
        url: "/api/upload",
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