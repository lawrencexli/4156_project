# COMS 4156 Project - Image Processing and Management Service (IPMS)

![Spring Boot 3.1.4](https://img.shields.io/badge/Spring%20Boot-3.1.4-brightgreen.svg)
![JDK 17](https://img.shields.io/badge/JDK-17-brightgreen.svg)
![MongoDB](https://img.shields.io/badge/MongoDB-6.0.10-darkgreen.svg)
![Spring Cloud GCP](https://img.shields.io/badge/SpringCloudGCP-4.8.0-brightgreen.svg)
![Gradle](https://img.shields.io/badge/Gradle-8.2.1-yellowgreen.svg)
![license](https://img.shields.io/badge/license-Apache--2.0-blue.svg)

## Team member
- Cole Donat (nad2168)
- Kiryl Beliauski (kb3338)
- Lawrence Li (ll3598)
- Tiger Shaw (cs4213)

## About the project

Image Processing and Management Service (IPMS) is a web service that 
offers a multi-faceted solution in computer vision with several features:

- **Image Repository:** Our clients can store and retrieve images based on unique 
image IDs with our service. By that, we will host and maintain persistent data 
storage in the form of images and their corresponding metadata. 

- **Transparency**: Our transparency feature allows clients to change the opacity 
of images from our image database. This is especially useful with our image overlay 
feature for watermarking while developing free-trial online photo editing apps. 
By using our service, clients can seamlessly generate watermarked images on-the-fly 
with specific transparency levels.

- **Cropping**: Our clients can use the cropping feature to trim or cut down images 
to their desired dimensions by specifying desired edge coordinates. This is useful 
for developers who may want to automatically crop user-uploaded images to fit a 
specific format or dimension for their app.

- **Change saturation**: With the saturation adjustment feature, our clients have the 
ability to adjust the intensity of colors in an image by specifying a desired value. 
Developers might use this feature to provide custom filters or effects that users can 
apply to images within an app or online service.

- **Image overlay (Not implemented yet)**: Our clients can use this feature of our 
service to overlay one image on top of another image from our image database, which 
can be particularly beneficial for watermarking, branding, or any other use cases 
where overlaying images are necessary.

## Build instructions

Here are the main required dependencies:
- JDK 17
- Gradle 8.2.1
- Spring Boot 3.1.4
- Spring Cloud GCP 4.8.0

Then, you will need to run the command that can prompt our Gradle 
to automatically download and install all required dependencies 
for running our service. 

Go to our repository, and run the following command:

For macOS/Linux:
`./gradlew bootRun`

Windows:
`.\gradlew.bat bootRun`

## Run instructions

1. Go to [IpmService.java](src/main/java/com/project/ipms/IpmService.java)
2. Run `IpmService` class.

Alternatively, you can also run the `./gradlew bootRun` or `.\gradlew.bat bootRun` 
command to start the service.

## Unit Test instructions

1. Go to [Test directory](src/test/java/com/project/ipms)
2. Run every test classes under the directory.

## API Test instructions

Our API entry points are tested using Postman. Download the app [here](https://www.postman.com/downloads/).

Here is how you set up the Postman API test:
1. Open the Postman app, go to **Settings**, and go to **Working directory**, set the location to our repository.
For example: `/path/to/our/repository/4156_project`
2. Click **Import** and select our repository folder. Postman will automatically 
detect API test files and import them accordingly. You should see an `ipms-api-test` collection file and an 
`ipms-api-test-environment` environment file. Import all of them. 
3. You should see `ipms-api-test` under **Collections**. On the top right hand corner of your screen, you should
also set up the environment as `ipms-api-test-environment`.
4. Start the service by following the **Run instructions** section above. You should have `http://localhost:8080`
set up. Go to the index page in your browser and check if `Welcome to IPMS!` message is displayed.
5. Now you can run the test by either running them one by one or run the whole collection.

------------------------------------------------------------------------------------------
## IPMS API Documentation

![version](https://img.shields.io/badge/Version-0.0.1--SNAPSHOT-brightgreen.svg)

[http://localhost:8080](http://localhost:8080)

#### **IMPORTANT:** 
- **Use `multipart/form-data` for all body arguments.**
- **For body arguments that require `int` or `float`, you can use `string` as long as they can be reasonably converted to required data type.**
- **For all non-file arguments, use `application/json` as `Content-Type`.**

------------------------------------------------------------------------------------------

### Index Page

<details>
 <summary><code>GET</code> <code><b>/</b></code></summary>

#### Responses

> | http code | content-type | response           |
> |-----------|--------------|--------------------|
> | 200       | string       | `Welcome to IPMS!` |

</details>

------------------------------------------------------------------------------------------

### Generate a client ID as API key credential

<details>
 <summary><code>GET</code> <code><b>/api/generate</b></code></summary>

#### Responses

> | http code | content-type       | response                                                                                |
> |-----------|--------------------|-----------------------------------------------------------------------------------------|
> | 200       | `application/json` | `{"responseMessage": {Your unique client ID as API key credential}, "statusCode": 200}` |

</details>

------------------------------------------------------------------------------------------

### Upload an image file

#### Supported image file extensions

- `png`
- `jpg`
- `jpeg`

<details>
 <summary><code>POST</code> <code><b>/api/upload</b></code></summary>

#### Parameters
##### Body

> | name | type     | data type           | description                                        |
> |------|----------|---------------------|----------------------------------------------------|
> | file | required | multipart/form-data | Uploaded image file contents via multipart request |

##### Header

> | name     | type     | data type | description                           |
> |----------|----------|-----------|---------------------------------------|
> | id       | required | string    | Your client ID credential             |

#### Responses

> | http code | content-type       | response                                                                                                                                     |
> |-----------|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
> | 200       | `application/json` | `{"responseMessage": "File uploaded successfully", "statusCode": 200}`                                                                       |
> | 400       | `application/json` | `{"responseMessage": "Image file validation failed: The file could be corrupted or is not an image file", "statusCode": 400}`                |
> | 400       | `application/json` | `{"responseMessage": "File has no content or is null", "statusCode": 400}`                                                                   |
> | 400       | `application/json` | `{"responseMessage": "Filename is empty or null", "statusCode": 400}`                                                                        |
> | 400       | `application/json` | `{"responseMessage": "Current request is not a multipart request", "statusCode": 400}`                                                       |
> | 400       | `application/json` | `{"responseMessage": "Client ID is missing or null", "statusCode": 400}`                                                                     |
> | 400       | `application/json` | `{"responseMessage": "Filename is missing file extension", "statusCode": 400}`                                                               |
> | 400       | `application/json` | `{"responseMessage": "Filename cannot start with a dot '.'", "statusCode": 400}`                                                             |
> | 403       | `application/json` | `{"responseMessage": "Invalid Client ID", "statusCode": 403}`                                                                                |
> | 409       | `application/json` | `{"responseMessage": "Filename already exists", "statusCode": 409}`                                                                          |
> | 415       | `application/json` | `{"responseMessage": "Not a supported file type. Currently, we support the following image file types: jpg, jpeg, png.", "statusCode": 415}` |                                                     
> | 500       | `application/json` | `{"responseMessage": {Generic error messages from IOException}, "statusCode": 500}`                                                          |

</details>

------------------------------------------------------------------------------------------

### Download an image file

<details>
 <summary><code>GET</code> <code><b>/api/download</b></code></summary>

#### Parameters
##### Query

> | name     | type     | data type | description                           |
> |----------|----------|-----------|---------------------------------------|
> | fileName | required | string    | The specified image file for download |

##### Header

> | name     | type     | data type | description                           |
> |----------|----------|-----------|---------------------------------------|
> | id       | required | string    | Your client ID credential             |

#### Responses

> | http code | content-type               | response                                                                                                                    |
> |-----------|----------------------------|-----------------------------------------------------------------------------------------------------------------------------|
> | 200       | `application/octet-stream` | Image file content download                                                                                                 |
> | 400       | `application/json`         | `{"responseMessage": "Filename is empty or null", "statusCode": 400}`                                                       |
> | 400       | `application/json`         | `{"responseMessage": "Client ID is missing or null", "statusCode": 400}`                                                    |
> | 403       | `application/json`         | `{"responseMessage": "Invalid Client ID", "statusCode": 403}`                                                               |
> | 404       | `application/json`         | `{"responseMessage": "File does not exist", "statusCode": 404}`                                                             |
> | 500       | `application/json`         | `{"responseMessage": "CRITICAL ERROR: File does not exist on GCP Bucket but exists in MongoDB records", "statusCode": 500}` |

</details>

------------------------------------------------------------------------------------------

### Make an image transparent

<details>
 <summary><code>PUT</code> <code><b>/api/transparent</b></code></summary>

#### Parameters
##### Body

> | name   | type     | data type | description                                            |
> |--------|----------|-----------|--------------------------------------------------------|
> | target | required | string    | Image filename targeted for processing                 |
> | result | required | string    | Desired filename for the image result after processing |
> | alpha  | required | float     | Desired alpha value for transparency                   |

##### Header

> | name     | type     | data type | description                           |
> |----------|----------|-----------|---------------------------------------|
> | id       | required | string    | Your client ID credential             |

#### Responses

> | http code | content-type       | response                                                                                                                    |
> |-----------|--------------------|-----------------------------------------------------------------------------------------------------------------------------|
> | 200       | `application/json` | `{"responseMessage": "Operation success", "statusCode": 200}`                                                               |
> | 400       | `application/json` | `{"responseMessage": "Target filename or result filename is empty or null", "statusCode": 400}`                             |
> | 400       | `application/json` | `{"responseMessage": "Client ID is missing or null", "statusCode": 400}`                                                    |
> | 400       | `application/json` | `{"responseMessage": "Target file extension is different from result file extension", "statusCode": 400}`                   |
> | 400       | `application/json` | `{"responseMessage": "The alpha value should be in the range of 0 to 1", "statusCode": 400}`                                |
> | 403       | `application/json` | `{"responseMessage": "Invalid Client ID", "statusCode": 403}`                                                               |
> | 404       | `application/json` | `{"responseMessage": "Target file does not exist", "statusCode": 404}`                                                      |
> | 409       | `application/json` | `{"responseMessage": "Result filename already exists", "statusCode": 409}`                                                  |
> | 500       | `application/json` | `{"responseMessage": "CRITICAL ERROR: File does not exist on GCP Bucket but exists in MongoDB records", "statusCode": 500}` |
> | 500       | `application/json` | `{"responseMessage": {Generic error messages from IOException}, "statusCode": 500}`                                         |

</details>

------------------------------------------------------------------------------------------

### Crop the image

<details>
 <summary><code>PUT</code> <code><b>/api/crop</b></code></summary>

#### Parameters
##### Body

> | name   | type     | data type | description                                            |
> |--------|----------|-----------|--------------------------------------------------------|
> | target | required | string    | Image filename targeted for processing                 |
> | result | required | string    | Desired filename for the image result after processing |
> | x      | required | int       | Upper left corner x value                              |
> | y      | required | int       | Upper left corner y value                              |
> | width  | required | int       | Width of the cropped region                            |
> | height | required | int       | Height of the cropped region                           |

##### Header

> | name     | type     | data type | description                           |
> |----------|----------|-----------|---------------------------------------|
> | id       | required | string    | Your client ID credential             |

#### Responses

> | http code | content-type       | response                                                                                                                    |
> |-----------|--------------------|-----------------------------------------------------------------------------------------------------------------------------|
> | 200       | `application/json` | `{"responseMessage": "Operation success", "statusCode": 200}`                                                               |
> | 400       | `application/json` | `{"responseMessage": "Target filename or result filename is empty or null", "statusCode": 400}`                             |
> | 400       | `application/json` | `{"responseMessage": "Client ID is missing or null", "statusCode": 400}`                                                    |
> | 400       | `application/json` | `{"responseMessage": "Target file extension is different from result file extension", "statusCode": 400}`                   |
> | 400       | `application/json` | `{"responseMessage": "The x value should be in the range of 0 to the width of the target image", "statusCode": 400}`        |
> | 400       | `application/json` | `{"responseMessage": "The y value should be in the range of 0 to the height of the target image", "statusCode": 400}`       |
> | 400       | `application/json` | `{"responseMessage": "The width value should be from 1 to (target image's width - x)", "statusCode": 400}`                  |
> | 400       | `application/json` | `{"responseMessage": "The height value should be from 1 to (target image's height - y)", "statusCode": 400}`                |
> | 403       | `application/json` | `{"responseMessage": "Invalid Client ID", "statusCode": 403}`                                                               |
> | 404       | `application/json` | `{"responseMessage": "Target file does not exist", "statusCode": 404}`                                                      |
> | 409       | `application/json` | `{"responseMessage": "Result filename already exists", "statusCode": 409}`                                                  |
> | 500       | `application/json` | `{"responseMessage": "CRITICAL ERROR: File does not exist on GCP Bucket but exists in MongoDB records", "statusCode": 500}` |
> | 500       | `application/json` | `{"responseMessage": {Generic error messages from IOException}, "statusCode": 500}`                                         |

</details>

------------------------------------------------------------------------------------------

### Change the saturation of an image

<details>
 <summary><code>PUT</code> <code><b>/api/saturation</b></code></summary>

#### Parameters
##### Body

> | name            | type     | data type | description                                            |
> |-----------------|----------|-----------|--------------------------------------------------------|
> | target          | required | string    | Image filename targeted for processing                 |
> | result          | required | string    | Desired filename for the image result after processing |
> | saturationCoeff | required | float     | Desired value to multiply saturation by (0-255)        |

##### Header

> | name     | type     | data type | description                           |
> |----------|----------|-----------|---------------------------------------|
> | id       | required | string    | Your client ID credential             |

#### Responses

> | http code | content-type       | response                                                                                                                    |
> |-----------|--------------------|-----------------------------------------------------------------------------------------------------------------------------|
> | 200       | `application/json` | `{"responseMessage": "Operation success", "statusCode": 200}`                                                               |
> | 400       | `application/json` | `{"responseMessage": "Target filename or result filename is empty or null", "statusCode": 400}`                             |
> | 400       | `application/json` | `{"responseMessage": "Client ID is missing or null", "statusCode": 400}`                                                    |
> | 400       | `application/json` | `{"responseMessage": "Target file extension is different from result file extension", "statusCode": 400}`                   |
> | 400       | `application/json` | `{"responseMessage": "The saturation coefficient should be in the range of 0 to 255", "statusCode": 400}`                   |
> | 403       | `application/json` | `{"responseMessage": "Invalid Client ID", "statusCode": 403}`                                                               |
> | 404       | `application/json` | `{"responseMessage": "Target file does not exist", "statusCode": 404}`                                                      |
> | 409       | `application/json` | `{"responseMessage": "Result filename already exists", "statusCode": 409}`                                                  |
> | 500       | `application/json` | `{"responseMessage": "CRITICAL ERROR: File does not exist on GCP Bucket but exists in MongoDB records", "statusCode": 500}` |
> | 500       | `application/json` | `{"responseMessage": {Generic error messages from IOException}, "statusCode": 500}`                                         |

</details>

------------------------------------------------------------------------------------------

### Image overlay

<details>
 <summary><code>PUT</code> <code><b>/api/overlay</b></code></summary>

#### Parameters
##### Body

> | name    | type     | data type | description                                            |
> |---------|----------|-----------|--------------------------------------------------------|
> | target1 | required | string    | Base image filename                                    |
> | target2 | required | string    | Image filename to be overlayed over the base image     |
> | result  | required | string    | Desired filename for the image result after processing |
> | x       | required | int       | Upper left corner x value for overlay                  |
> | y       | required | int       | Upper left corner y value for overlay                  |

##### Header

> | name     | type     | data type | description                           |
> |----------|----------|-----------|---------------------------------------|
> | id       | required | string    | Your client ID credential             |

#### Responses

> | http code | content-type       | response                                                                                                                    |
> |-----------|--------------------|-----------------------------------------------------------------------------------------------------------------------------|
> | 200       | `application/json` | `{"responseMessage": "Operation success", "statusCode": 200}`                                                               |
> | 400       | `application/json` | `{"responseMessage": "Target filename or result filename is empty or null", "statusCode": 400}`                             |
> | 400       | `application/json` | `{"responseMessage": "Client ID is missing or null", "statusCode": 400}`                                                    |
> | 400       | `application/json` | `{"responseMessage": "Target file extension is different from result file extension", "statusCode": 400}`                   |
> | 400       | `application/json` | `{"responseMessage": "The x value should be in the range of 0 to the width of the target image"                             |
> | 400       | `application/json` | `{"responseMessage": "The y value should be in the range of 0 to the height of the target image"                            |
> | 403       | `application/json` | `{"responseMessage": "Invalid Client ID", "statusCode": 403}`                                                               |
> | 404       | `application/json` | `{"responseMessage": "Target file does not exist", "statusCode": 404}`                                                      |
> | 409       | `application/json` | `{"responseMessage": "Result filename already exists", "statusCode": 409}`                                                  |
> | 500       | `application/json` | `{"responseMessage": "CRITICAL ERROR: File does not exist on GCP Bucket but exists in MongoDB records", "statusCode": 500}` |
> | 500       | `application/json` | `{"responseMessage": {Generic error messages from IOException}, "statusCode": 500}`                                         |

</details>

------------------------------------------------------------------------------------------






