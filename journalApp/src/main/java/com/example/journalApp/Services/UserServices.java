package com.example.journalApp.Services;

import com.example.journalApp.Entity.User;
import com.example.journalApp.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveUserWithoutPasswordEncoding(User newUser) {
        userRepository.save(newUser);
    }

    public boolean saveNewUser(User newUser) {
        try {
            // encoding the given password and saving it inside db
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            newUser.setRoles(Arrays.asList("USER"));
            userRepository.save(newUser);
            return true;
        }
        catch (Exception e) {
            log.error("Error occurred for {} : ", newUser.getUsername(), e);
            log.debug("Debug started.");
            log.trace("start from here boi.");
            return false;
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(ObjectId userId) {
        return userRepository.findById(userId);
    }

    public void deleteUserById(ObjectId userId) {
        userRepository.deleteById(userId);
    }

    public void saveAdmin(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRoles(Arrays.asList("USER", "ADMIN"));
        userRepository.save(newUser);
    }
}



/*
LOG details ::

Instead of using -- private static final Logger logger = LoggerFactory.getLogger(JournalEntryServices.class);
in all the class, we can actually use the @Slf4j annotation (a lombok annotation), and with that we don't need
to create an instance of in every class. In the runtime it will automatically create the instance of logger for
us. Only thing is, here it will be "log" instead of the name we give like "logger".

Such as: log.error();


There are total 5 levels of log:
Trace, debug, info, warn, error.

By default, info, error and warn logs will be available and we can use them anywhere we want in the code.
But to use debug and trace, we need to enable them from the application.properties or application.yml file.

This is how we can enable them from a .yml file:
logging:
    level:
        com:
            example:
                journalApp: DEBUG

the com.example.journalApp is from the package.

Similarly, in .properties file we can do it this way:
logging.level.com.example.journalApp = TRACE


Here since we are using the whole package, the DEBUG or TRACE will be enabled for the entire package.

Also, the level we are giving here based on that the other logs will be printed. The level is lowest
for TRACE and highest for ERROR.

So if we keep the level as TRACE, all the logs (including DEBUG) will be printed.
And if we keep the level as ERROR, (since it has the highest level) only ERROR logs will be printed.

After setting the log level till the package, only logs inside package is set. So while starting the application, the
logs we usually see (which are out of the package logs) will still be logged.

And if we want to control all the logs (even the outer package logs as well)... then we need to write like this:

logging:
    level:
        root: ERROR

Now from the entire root package, only the error logs will be printed, and everything else will be ignored.



Sometimes, for debug purpose we might add too many logs in a code package... and because of that we are not able to read
the other stuffs properly, but for future debugging purpose we don't want to get rid of those logs. For that we have
a way:

logging:
    level:
        com:
            example:
                journalApp:
                    Services:
                        UserServices: OFF

OR

logging.level.com.example.journalApp.Services = OFF

Through this, we can turn off the log printing of that particular package. It is not only specific to a package,
we can do this for a specific class as well.


 */


package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.exception.CustomFileException;
import com.example.product.repository.ProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void processExcelFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomFileException("The uploaded file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
            throw new CustomFileException("Invalid file format. Only Excel files are supported.");
        }

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() == 0) {
                throw new CustomFileException("The Excel file is empty.");
            }

            List<Product> products = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) { // Skip header row
                    continue;
                }

                Product product = parseRowToProduct(row);
                products.add(product);
            }

            productRepository.saveAll(products);

        } catch (Exception e) {
            throw new CustomFileException("Error processing Excel file: " + e.getMessage());
        }
    }

    private Product parseRowToProduct(Row row) {
        try {
            String productName = getCellValue(row.getCell(0));
            String description = getCellValue(row.getCell(1));

            if (productName == null || productName.isEmpty()) {
                throw new CustomFileException("Product name cannot be empty in row " + (row.getRowNum() + 1));
            }

            Product product = new Product();
            product.setProductName(productName);
            product.setDescription(description);

            return product;

        } catch (Exception e) {
            throw new CustomFileException("Error parsing row " + (row.getRowNum() + 1) + ": " + e.getMessage());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }

        return null;
    }
}





package com.example.product.exception;

public class CustomFileException extends RuntimeException {
    public CustomFileException(String message) {
        super(message);
    }
}






