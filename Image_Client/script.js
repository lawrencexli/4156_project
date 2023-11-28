'use strict'
let uploaded_img;
let id = '';
let selected_img;
let op = 0;

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
        if (get_selected_img()) {
            let x = $("#img_x").val();
            let y = $("#img_y").val();
            let width = $("#img_w").val();
            let height = $("#img_h").val();
            // console.log(x, y, width, height);
            img_crop(x, y, width, height);
        }
    });
});

$(function () {
    $('#img_tran_btn').click(function () {
        if (get_selected_img()) {
            let alpha = $("#alpha").val();
            img_trans(alpha);
            // console.log(alpha);
        }
    });
});

$(function () {
    $('#img_satu_btn').click(function () {
        if (get_selected_img()) {
            let sat = $("#sat_coeff").val();
            img_satu(sat);
            // console.log(values);
        }
    });
});

$(function () {
    $("#img_download_btn").click(function () {
        if (get_selected_img()) {
            img_download();
        }
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
    // formData.append("id", id);
    formData.append("file", uploaded_img);

    const response = await fetch("http://localhost:8080/api/upload", {
        method: "POST",
        body: formData,
        headers: {
            'id': id,
        },
    });
    const data = await response.json();
    console.log(data);

    if (response.ok) {
        let option = '<option>' + uploaded_img.name + '</option>';
        $("#img_dropdown").append(option);
    }
}

async function img_download() {
    let formData = new FormData();
    formData.append("id", id);
    formData.append("fileName", selected_img);

    const response = await fetch("http://localhost:8080/api/download?" + new URLSearchParams({
        "fileName": selected_img
    }), {
        method: "GET",
        mode: "cors",
        headers: {
            'id': id,
        },
    });

    if (response.ok) {
        const myBlob = await response.blob();
        var a = document.createElement("a");
        const url = window.URL.createObjectURL(myBlob);
        a.href = url;
        a.download = selected_img;
        a.click();
        window.URL.revokeObjectURL(url);
    } else {
        const data = await response.json();
        console.log(data);
    }
}

async function img_trans(alpha) {
    let formData = new FormData();

    let result_img = "alpha" + alpha + "_" + op.toString() + "_" + selected_img;
    op++;

    // formData.append("id", id);
    formData.append("target", selected_img);
    formData.append("result", result_img);
    formData.append("alpha", alpha);

    const response = await fetch("http://localhost:8080/api/transparent", {
        method: "PUT",
        body: formData,
        headers: {
            'id': id,
        },
    });
    const data = await response.json();
    console.log(data);

    if (response.ok) {
        let option = '<option>' + result_img + '</option>';
        $("#img_dropdown").append(option);
    }
}

async function img_crop(x, y, width, height) {
    let formData = new FormData();
    let result_img = "crop" + op.toString() + "_" + selected_img;
    op++;

    // formData.append("id", id);
    formData.append("target", selected_img);
    formData.append("result", result_img);
    formData.append("x", x);
    formData.append("y", y);
    formData.append("width", width);
    formData.append("height", height);

    const response = await fetch("http://localhost:8080/api/crop", {
        method: "PUT",
        body: formData,
        headers: {
            'id': id,
        },
    });
    const data = await response.json();
    console.log(data);

    if (response.ok) {
        let option = '<option>' + result_img + '</option>';
        $("#img_dropdown").append(option);
    } else {
        alert(data.responseMessage);
    }
}

async function img_satu(saturationCoeff) {
    let formData = new FormData();

    let result_img = "sat_" + saturationCoeff + "_" + op.toString() + "_" + selected_img;
    op++;

    // formData.append("id", id);
    formData.append("target", selected_img);
    formData.append("result", result_img);
    formData.append("saturationCoeff", saturationCoeff);

    const response = await fetch("http://localhost:8080/api/saturation", {
        method: "PUT",
        body: formData,
        headers: {
            'id': id,
        },
    });
    const data = await response.json();
    console.log(data);

    if (response.ok) {
        let option = '<option>' + result_img + '</option>';
        $("#img_dropdown").append(option);
    }
}

function get_selected_img() {
    selected_img = $("#img_dropdown :selected").val();
    if (selected_img == "--select--") {
        return false;
    } else {
        return true;
    }
}

