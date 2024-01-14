# HCvision - autoML Hierarchical Clustering Visualiser

HCvision is currently under development as a component of my bachelor's thesis in computer science at the [International Hellenic University.](https://www.iee.ihu.gr/)

## About

This application will allow users to upload datasets on which they wish to perform cluster analysis, help them determine both the number of clusters and the appropriate cluster proximity method, perform the clustering, and present the user with the results. 
The application will have a user-friendly web interface and a WEB API which will allow all operations to be performed via http requests. 

---------------------------------------------------------------------------------------------

## Backend - API

### Technologies Used

- Spring Boot
- MySQL
- Python

### Database Schema

#### `users` Table


| Column      | Data Type    | Constraints           | Description                    |
|-------------|--------------|-----------------------|--------------------------------|
| id          | bigint       | NOT NULL AUTO_INCREMENT| User ID (Primary Key)          |
| first_name  | varchar(255) | DEFAULT NULL          | User's first name              |
| last_name   | varchar(255) | DEFAULT NULL          | User's last name               |
| email       | varchar(255) | DEFAULT NULL          | User email address             |
| password    | varchar(255) | DEFAULT NULL          | User password                  |
| role        | tinyint      | DEFAULT NULL          | User role (0 or 1)             |
| activated   | bit(1)       | NOT NULL              | Account activation status      |




#### `confirmation_token` Table

| Column        | Data Type      | Constraints   | Description                        |
|---------------|----------------|---------------|------------------------------------|
| id            | bigint         | NOT NULL      | Token ID (Primary Key)             |
| type          | tinyint        | DEFAULT NULL  | Type of confirmation token         |
| token         | varchar(255)   | NOT NULL      | Confirmation token value          |
| confirmed_at  | datetime(6)    | DEFAULT NULL  | Date and time of confirmation      |
| created_at    | datetime(6)    | NOT NULL      | Date and time of creation          |
| expires_at    | datetime(6)    | NOT NULL      | Expiry date and time               |
| user_id       | bigint         | NOT NULL      | User ID (Foreign Key)              |

#### `dataset` Table

| Column                  | Data Type      | Constraints   | Description                       |
|-------------------------|----------------|---------------|-----------------------------------|
| id                      | bigint         | NOT NULL      | Dataset ID (Primary Key)          |
| access_type             | tinyint        | DEFAULT NULL  | Type of dataset access            |
| file_name               | varchar(255)   | DEFAULT NULL  | Name of the dataset file           |
| path                    | varchar(255)   | DEFAULT NULL  | Path to the dataset                |
| numeric_cols            | varchar(255)   | DEFAULT NULL  | Numeric columns information        |
| user_id                 | bigint         | NOT NULL      | User ID (Foreign Key)              |

#### `optimal` Table

| Column                     | Data Type      | Constraints   | Description                           |
|----------------------------|----------------|---------------|---------------------------------------|
| id                         | bigint         | NOT NULL      | Optimal process ID (Primary Key)      |
| user_id                    | bigint         | NOT NULL      | User ID (Foreign Key)                 |
| dataset_id                 | bigint         | NOT NULL      | Dataset ID (Foreign Key)              |
| sample                     | bit(1)         | NOT NULL      | Sample indicator                      |
| attributes                 | varchar(255)   | DEFAULT NULL  | Information about attributes          |
| inconsistency_coefficient  | varchar(255)   | DEFAULT NULL  | Inconsistency coefficient            |
| status                     | tinyint        | DEFAULT NULL  | Status of the optimal process         |
| started_at                 | datetime(6)    | DEFAULT NULL  | Start date and time of the process    |
| ended_at                   | datetime(6)    | DEFAULT NULL  | End date and time of the process      |
| duration                   | bigint         | NOT NULL      | Duration of the process               |

#### `analysis` Table

| Column                        | Data Type      | Constraints   | Description                              |
|-------------------------------|----------------|---------------|------------------------------------------|
| id                            | bigint         | NOT NULL      | Analysis process ID (Primary Key)         |
| user_id                       | bigint         | NOT NULL      | User ID (Foreign Key)                    |
| dataset_id                    | bigint         | NOT NULL      | Dataset ID (Foreign Key)                 |
| sample                        | bit(1)         | NOT NULL      | Sample indicator                         |
| linkage                       | tinyint        | DEFAULT NULL  | Linkage type                             |
| num_clusters                  | int            | NOT NULL      | Number of clusters                       |
| attributes                    | varchar(255)   | DEFAULT NULL  | Information about attributes             |
| cluster_assignment_result_path| varchar(255)   | DEFAULT NULL  | Path to cluster assignment result        |
| dendrogram_result_path        | varchar(255)   | DEFAULT NULL  | Path to dendrogram result                 |
| parallel_coordinates_result_path| varchar(255) | DEFAULT NULL  | Path to parallel coordinates result      |
| status                        | tinyint        | DEFAULT NULL  | Status of the analysis process           |
| started_at                    | datetime(6)    | DEFAULT NULL  | Start date and time of the process       |
| ended_at                      | datetime(6)    | DEFAULT NULL  | End date and time of the process         |
| duration                      | bigint         | NOT NULL      | Duration of the process                  |

#### `history` Table

| Column          | Data Type      | Constraints   | Description                          |
|-----------------|----------------|---------------|--------------------------------------|
| id              | bigint         | NOT NULL      | History entry ID (Primary Key)        |
| user_id         | bigint         | NOT NULL      | User ID (Foreign Key)                 |
| optimal_id      | bigint         | DEFAULT NULL  | Optimal ID (Foreign Key)              |
| analysis_id     | bigint         | DEFAULT NULL  | Analysis ID (Foreign Key)             |
| current_script  | varchar(255)   | DEFAULT NULL  | Path to the current script            |
| time_started    | datetime(6)    | DEFAULT NULL  | Start date and time of the history entry |

### Endpoints

#### Authentication

- **POST** `/api/v1/auth/authenticate` - Authenticate User
- **POST** `/api/v1/auth/register` - Register User
- **GET** `/api/v1/auth/confirm` - Confirm Email
- **GET** `/api/v1/auth/confirmation-link` - Request Email Confirmation

#### Users

- **GET** `/api/v1/users/` - Retrieve User Information
- **POST** `/api/v1/users/update` - Update User Information
- **DELETE** `/api/v1/users/delete` - Delete User
- **POST** `/api/v1/users/password/forgot` - Request Password Reset
- **POST** `/api/v1/users/password/reset` - Reset User Password

#### Datasets

- **GET** `/api/v1/datasets/` - Retrieve Public and Private Datasets
- **DELETE** `/api/v1/datasets/delete` - Delete Dataset
- **GET** `/api/v1/datasets/download` - Download Dataset
- **POST** `/api/v1/datasets/upload` - Upload Dataset

#### Clustering

- **GET** `/api/v1/hierarchical/optimal` - Execute 'Optimal Parameter Calculation'
- **GET** `/api/v1/hierarchical/analysis` - Execute 'Hierarchical Clustering'

#### History

- **GET** `/api/v1/hierarchical/history/` - Retrieve List of History Entries
- **GET** `/api/v1/hierarchical/history/{id}` - Retrieve Information of a Specific History Entry
- **DELETE** `/api/v1/hierarchical/history/{id}` - Delete History Entry

#### Results

- **GET** `/api/v1/resources/analysis/` - Retrieve Clustering Results
- **GET** `/api/v1/resources/optimal/` - Retrieve Optimal Parameter Results


### Environmental Variables

To run the server successfully, ensure the following environmental variables are set with their corresponding values:

- **DB_PASSWORD**: Password for the MySQL database.

- **DB_URL**: URL for the MySQL database, including connection details.

- **DB_USER**: Username for connecting to the MySQL database.

- **LOGGING_FILE**: Path to the log file for application logs.

- **MAIL_CONNECTION_TIMEOUT**: Connection timeout for email.

- **MAIL_HOST**: Hostname for the mail server.

- **MAIL_PASSWORD**: Password for the email account.

- **MAIL_PORT**: Port number for the mail server.

- **MAIL_TIMEOUT**: Timeout for email operations.

- **MAIL_USERNAME**: Username for the email account.

- **MAIL_WRITETIMEOUT**: Write timeout for email operations.

- **MULTIPART_MAX_FILE_SIZE**: Maximum allowed file size for multipart requests.

- **MULTIPART_MAX_REQUEST_SIZE**: Maximum allowed request size for multipart requests.

- **SECRET_KEY**: Secret key for securing the application.

- **SECRET_KEY_EXPIRATION**: Expiration time for the secret key.

- **THREADS**: Number of threads for the application.

Ensure these environmental variables are correctly set with the appropriate values before running the server to ensure proper functionality.


### Python Configuration

Ensure the server has a Python 3 virtual environment (venv) set up with the following dependencies installed. Exclude any duplicate dependencies or those already included in the default Python installation:

```
# Step 1: Create and activate a Python 3 virtual environment
python3 -m venv [your_venv_name]
source [your_venv_name]/bin/activate

# Step 2: Install required Python dependencies
pip install pandas scipy scikit-learn matplotlib

# Step 3: Verify the installation
python -c "import pandas, scipy, sklearn, matplotlib"
```

### Compiling and Running the Application

Follow these steps to compile the JAR file and run the application:

```
Step 1: Clone the Repository
git clone [repository_url]
cd [repository_directory]

Step 2: Compile the Code
mvn clean package

Step 3: Set Environmental Variables
export DB_PASSWORD=your_db_password
export DB_URL=your_db_url
export DB_USER=your_db_user
export LOGGING_FILE=./logs/app.log
... (set other variables accordingly)

Step 4: Run the Application
java -jar target/[your_jar_file_name].jar
Replace [your_jar_file_name] with the actual name of the JAR file generated in the target directory.

```
## Client

### Technologies Used

- Angular
- Angular Material
- TypeScript

### Pages

1. **Home**
   - Description: Landing page with an overview of the application.
   
2. **About**
   - Description: Information about the application and its purpose.

3. **Datasets**
   - Description: View and manage datasets.

4. **Hierarchical**
   - Description: Execute hierarchical clustering on datasets.

5. **History**
   - Description: View history and details of past activities.

6. **Api Docs**
   - Description: Documentation for the API endpoints.

7. **Rate Us**
   - Description: Provide feedback and rate the application.

8. **Profile**
   - Description: Manage user profile settings.

### How to Compile and Deploy

#### Compile Angular Project

1. Install Node.js and npm if not already installed.
2. Open a terminal and navigate to the client project directory.
3. Run the following commands:

    ```bash
    # Install dependencies
    npm install

    # Build the project
    ng build
    ```

#### Deploy Compiled Files

After running the build command, the compiled files will be available in the `dist/` directory. You can deploy these files to a web server, or host them using services like Netlify, Vercel, or GitHub Pages.

If using a simple web server, you can deploy by copying the contents of the `dist/` directory to the server's public directory.

Example using a simple Python server:

```bash
# Install Python's http server (if not installed)
npm install -g http-server

# Navigate to the dist/ directory
cd dist/

# Start the server
http-server





