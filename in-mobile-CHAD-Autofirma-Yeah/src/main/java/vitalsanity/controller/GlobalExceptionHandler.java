package vitalsanity.controller;

import vitalsanity.controller.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import vitalsanity.controller.exception.NotFoundException;

/**
 * Global exception handler to manage application-wide exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException(NotFoundException ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", ex.getMessage());
        mav.setViewName("error/404"); // Ensure you have a 404.html in src/main/resources/templates/error/
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

}


