# COMS 4156 Project - Image Processing and Management Service (IPMS)

![Spring Boot 3.1.4](https://img.shields.io/badge/Spring%20Boot-3.1.4-brightgreen.svg)
![JDK 17](https://img.shields.io/badge/JDK-17-brightgreen.svg)
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

## Build instructions

Here are the main required dependencies:
- JDK 17
- Gradle 8.2.1
- Spring Boot 3.1.4
- Spring Cloud GCP 4.8.0

Then, you will need to run the command that can prompt our Gradle 
to automatically download and install all required dependencies 
for running our service. 

Go to our respository, and run the following command:

For macOS/Linux:
`./gradlew bootRun`

Windows:
`.\gradlew.bat bootRun`

## Run instructions

1. Go to [IpmService.java](src/main/java/com/project/ipms/IpmService.java)
2. Run `IpmService` class.

## Test instructions

1. Go to [Test directory](src/test/java/com/project/ipms)
2. Run every test classes under the directory.

------------------------------------------------------------------------------------------
## IPMS API Documentation

![version](https://img.shields.io/badge/Version-0.0.1--SNAPSHOT-brightgreen.svg)

[http://localhost:8080](http://localhost:8080)

------------------------------------------------------------------------------------------

### Generate a client ID as API key credential

<details>
 <summary><code>GET</code> <code><b>/api/generate</b></code></summary>

#### Responses

> | http code | content-type       | response                                                                                |
> |-----------|--------------------|-----------------------------------------------------------------------------------------|
> | `200`     | `application/json` | `{"responseMessage": {Your unique client ID as API key credential}, "statusCode": 200}` |

</details>

------------------------------------------------------------------------------------------

### Upload an image file

#### Supported image file extensions

- `png`
- `jpg`
- `jpeg`

<details>
 <summary><code>POST</code> <code><b>/api/upload?id={clientID}</b></code></summary>

#### Parameters

> | name       | type     | data type           | description                                        |
> |------------|----------|---------------------|----------------------------------------------------|
> | `clientID` | required | string              | Your client ID credential                          |
> | file       | required | multipart/form-data | Uploaded image file contents via multipart request |

#### Responses

> | http code | content-type       | response                                                                                                                  |
> |-----------|--------------------|---------------------------------------------------------------------------------------------------------------------------|
> | `200`     | `application/json` | `{"responseMessage": "File uploaded successfully", "statusCode": 200}`                                                    |
> | `400`     | `application/json` | `{"responseMessage": "File has no content or file is null", "statusCode": 400}`                                           |
> | `400`     | `application/json` | `{"responseMessage": "Current request is not a multipart request", "statusCode": 400}`                                    |
> | `400`     | `application/json` | `{"responseMessage": "Client ID is missing or is null", "statusCode": 400}`                                               |
> | `400`     | `application/json` | `{"responseMessage": "Filename already exists", "statusCode": 400}`                                                       |
> | `403`     | `application/json` | `{"responseMessage": "Invalid Client ID", "statusCode": 403}`                                                             |
> | `415`     | `application/json` | `{"responseMessage": "Not a supported file type", "statusCode": 415}`                                                     |                                                     
> | `500`     | `application/json` | `{"responseMessage": {Generic error messages from IOException}, "statusCode": 500}`                                       |

</details>

------------------------------------------------------------------------------------------

### Download an image file

<details>
 <summary><code>GET</code> <code><b>/api/download?id={clientID}&fileName={fileID}</b></code></summary>

#### Parameters

> | name       | type     | data type | description                           |
> |------------|----------|-----------|---------------------------------------|
> | `clientID` | required | string    | Your client ID credential             |
> | `fileID`   | required | string    | The specified image file for download |

#### Responses

> | http code | content-type               | response                                                                                                                    |
> |-----------|----------------------------|-----------------------------------------------------------------------------------------------------------------------------|
> | `200`     | `application/octet-stream` | Image file content download                                                                                                 |
> | `400`     | `application/json`         | `{"responseMessage": "Filename is null or empty", "statusCode": 400}`                                                       |
> | `400`     | `application/json`         | `{"responseMessage": "Client ID is missing or is null", "statusCode": 400}`                                                 |
> | `403`     | `application/json`         | `{"responseMessage": "Invalid Client ID", "statusCode": 403}`                                                               |
> | `404`     | `application/json`         | `{"responseMessage": "File does not exist", "statusCode": 404}`                                                             |
> | `500`     | `application/json`         | `{"responseMessage": "CRITICAL ERROR: File does not exist on GCP Bucket but exists in MongoDB records", "statusCode": 500}` |

</details>

------------------------------------------------------------------------------------------








