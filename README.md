# HCvision - AutoML Hierarchical Clustering Visualizer

HCvision was developed as part of my bachelor's thesis in Computer Science at the [International Hellenic University](https://www.iee.ihu.gr/). It is a fully functional AutoML application designed to automate hierarchical agglomerative clustering (HAC) on datasets. The application allows users to upload datasets, determine the optimal number of clusters, select the appropriate proximity method, and visualize the clustering results, all through an intuitive user interface. HCvision provides both a web interface and a Web API to facilitate clustering operations and integration with other systems. The backend is built using **Spring Boot**, **MySQL**, and **Python**, while the frontend client is developed with **Angular**, offering a responsive and user-friendly experience.

<img src="/screenshots/hcvision-home.png" width="300">
<img src="/screenshots/hcvision-optimal.png" width="300">
<img src="/screenshots/hcvision-analysis.png" width="300">
<img src="/screenshots/hcvision-dataset.png" width="300">
<img src="/screenshots/hcvision-history.png" width="300">

---------------------------------------------------------------------------------------------

## Features

### 1. **User Management**
- **Registration & Authentication**: Users can register, log in, and access the platform securely.
- **Profile Management**: Update or delete account details directly via the **Profile** page.

### 2. **Dataset Management**
- **Dataset Uploads**: Upload datasets in either CSV or XLS format with ease.
- **Dataset Preview**: Manage uploaded datasets and preview their contents via the **Dataset** page.

### 3. **Optimal Parameter Calculation**
- **Automated Parameter Selection**: Determine the optimal number of clusters and the most suitable cluster proximity method using the **Optimal Parameters** feature.
- **Visual Representation**: View a detailed visualization of the findings for better understanding.

### 4. **Hierarchical Clustering Analysis**
- **Customizable Analysis**: Perform clustering analysis using parameters derived from the **Optimal Parameter Calculation** step.
- **Dendrograms and Visualizations**: Generate a dendrogram and parallel coordinates plot to visualize the clustering process.
- **Cluster Assignments**: Obtain clear cluster assignments for every data point.

### 5. **Export Results**
- **Visualizations Export**: Export visuals in png format for further analysis or reporting.
- **Cluster Export**: Export cluster assignments in CSV format for further analysis or reporting.

### 6. **History Tracking**
- **Analysis History**: Automatically record every analysis performed.
- **Revisit and Replay**: Re-run or review results of past analyses via the **History** page.

### 7. **Asynchronous Processing**
- **Non-blocking Operations**: Initiate clustering algorithms and allow them to run asynchronously in the background.
- **Log in Later**: Start a process and return at a convenient time to review the results once the analysis is complete.


---------------------------------------------------------------------------------------------

## Technologies Used

The application is built using a combination of powerful technologies to ensure scalability, security, and efficient processing across both the backend and frontend. Below are the key technologies utilized in the project:

- **Spring Boot**: A Java-based framework that simplifies the development of stand-alone, production-grade applications. It is used to build the backend API and handle HTTP requests, providing a robust foundation for the server-side logic.

- **MySQL**: A relational database management system used to store and manage data, including datasets, clustering results, and user-related information.

- **JWT Authentication**: JSON Web Tokens (JWT) are used to secure the application by providing stateless authentication. JWT ensures that only authorized users can access sensitive endpoints.

- **Swagger**: A tool for API documentation and testing, integrated into the backend to automatically generate interactive API documentation. It allows developers and users to interact with and test API endpoints efficiently.

- **Python**: Utilized for the machine learning components, particularly for clustering analysis. Python's powerful libraries are leveraged, including:
   - **Scikit-learn**: A library providing efficient tools for data mining and machine learning, specifically for hierarchical agglomerative clustering.
   - **Pandas**: Used for data manipulation and analysis, allowing efficient handling of datasets before passing them into machine learning algorithms.
   - **Matplotlib**: Used to generate visualizations and graphs, helping represent the clustering results.

- **Angular**: A framework used to build the interactive user interface, enabling seamless communication with the backend API. It allows users to manage datasets, perform clustering operations, and view results through an intuitive and responsive web interface.


---------------------------------------------------------------------------------------------


## How to Run the Backend

Follow the steps below to set up and run the HCvision backend application.

### 1. Clone the Repository

First, clone the repository to your local machine using Git:

```bash
git clone [repository_url]
cd [repository_directory]
```


### 2. Set Up Environment Variables
You can export these variables directly from the command line before running the backend application. Alternatively, you can define the environment variables directly in the application.yml configuration file.

```config
export DB_PASSWORD=your_db_password
export DB_URL=your_db_url
export DB_USER=your_db_user
export LOGGING_FILE=./logs/app.log
export MAIL_CONNECTION_TIMEOUT=your_mail_connection_timeout
export MAIL_HOST=your_mail_host
export MAIL_PASSWORD=your_mail_password
export MAIL_PORT=your_mail_port
export MAIL_TIMEOUT=your_mail_timeout
export MAIL_USERNAME=your_mail_username
export MAIL_WRITETIMEOUT=your_mail_writetimeout
export MULTIPART_MAX_FILE_SIZE=your_max_file_size
export MULTIPART_MAX_REQUEST_SIZE=your_max_request_size
export SECRET_KEY=your_secret_key
export SECRET_KEY_EXPIRATION=your_secret_key_expiration_time
export THREADS=your_thread_count
```

### 3. Python Configuration

- Create and Activate a Python Virtual Environment
   ```bash
   python3 -m venv [your_venv_name]
   source [your_venv_name]/bin/activate  # For macOS/Linux
   [your_venv_name]\Scripts\activate     # For Windows
   ```
- Install Required Python Dependencies
   ```
   pip install pandas scipy scikit-learn matplotlib
   ```
### 4. Compile the Backend Application
   ```
   mvn clean package
   ```

### 5. Run the Application
   ```
   java -jar target/[your_jar_file_name].jar
   ```


---------------------------------------------------------------------------------------------


## How to Run the Client

### 1. Prerequisites

Before running the client, make sure you have the following tools installed on your system:

- **Node.js**: Required for running the Angular development server and building the project.
- **npm** (Node Package Manager): Comes with Node.js and is used to manage project dependencies.
- **Angular CLI**: A command-line interface tool for managing Angular applications.

You can install Node.js and npm from [here](https://nodejs.org/).

To install Angular CLI, run:

```bash
npm install -g @angular/cli
```

### 2. Install Dependencies
- Navigate to the client project directory and install the necessary dependencies using npm:
```
cd [client_directory]
npm install
```
This will install all the required dependencies listed in the package.json file, including Angular and Angular Material.

### 3. Compile the Angular Project
```
ng build --prod
```
### 4. Run the Development Server
```
ng serve
```
### 5. Deploying the Compiled Application
- You can deploy the files by copying the contents of the dist/ directory to your web server's public directory.






