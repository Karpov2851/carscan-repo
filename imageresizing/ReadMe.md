# Lambda Handler to handle S3 put event

### Salient Features
* Scales the image to 400 * 400 and saves it in the bucket and removes the original image since
as per the problem statement which asserts that the size of the storage server is 15 mb
* Lambda logger in place to view the application logs in cloudwatch