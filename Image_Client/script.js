'use strict'
let uploaded_img;
let id = '';
let selected_img;

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
        uploaded_img = this.files[0];
        $("#uploaded_img").empty();
        $("#uploaded_img").append(fileName + '<br>')
    });
});

$(function () {
    $('#img_crop_btn').click(function () {
        if(!get_selected_img()){
            return;
        }
        let x = $("#img_x").val();
        let y = $("#img_y").val();
        console.log(x, y);
    });
});

$(function () {
    $('#img_tran_btn').click(function () {
        if(!get_selected_img()){
            return;
        }
        let values = $("#alpha").val();
        console.log(values);
    });
});

$(function () {
    $('#img_satu_btn').click(function () {
        if(!get_selected_img()){
            return;
        }
        let values = $("#sat_coeff").val();
        console.log(values);
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

    let option = '<option>' + uploaded_img.name + '</option>';
    $("#img_dropdown").append(option);
}

function get_selected_img(){
    selected_img = $("#img_dropdown :selected").val();
    if (selected_img == "--select--"){
        return false;
    }else{
        return true;
    }
}