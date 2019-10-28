package net.wftutorials.projects.springboot.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.wftutorials.projects.springboot.web.model.Todo;
import net.wftutorials.projects.springboot.web.service.TodoService;

@Controller
public class TodoController {

  @Autowired
  TodoService todoService;
  
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    //Date - dd/MM/yyyy
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
  }

  @RequestMapping(value = "/list-todos", method = RequestMethod.GET)
  public String showTodos(ModelMap model) {
    String name = getLoggedInUsername();
    model.put("todos", todoService.retrieveTodos(name));
    return "list-todos";
  }

  private String getLoggedInUsername() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if(principal instanceof UserDetails) {
      return ((UserDetails) principal).getUsername();
    }
    return principal.toString();
  }

  @RequestMapping(value = "/add-todo", method = RequestMethod.GET)
  public String showAddTodoPage(ModelMap model) {
      model.addAttribute("todo", new Todo(0, getLoggedInUsername(), "Default Desc",
              new Date(), false));
      return "todo";
  }

  @RequestMapping(value = "/delete-todo", method = RequestMethod.GET)
  public String deleteTodo(ModelMap model, @RequestParam int id) {
    todoService.deleteTodo(id);
    return "redirect:/list-todos";
  }
  
  @RequestMapping(value = "/update-todo", method = RequestMethod.GET)
  public String showUpdateTodoPage(ModelMap model, @RequestParam int id) {
    Todo taskToUpdate = todoService.getTodo(id);
    model.addAttribute("todo", taskToUpdate);
    return "todo";
  }
  
  @RequestMapping(value = "/update-todo", method = RequestMethod.POST)
  public String updateTodo(ModelMap model,@Valid Todo todo, BindingResult result) {
    if(result.hasErrors()) {
      return "todo";
    }
    todo.setUser(getLoggedInUsername());
    todoService.updateTodo(todo);
    return "redirect:/list-todos";
  }

  @RequestMapping(value = "/add-todo", method = RequestMethod.POST)
  public String addTodo(ModelMap model, @Valid Todo todo, BindingResult result) {
    if(result.hasErrors()) {
      return "todo";
    }
    todoService.addTodo(getLoggedInUsername(), todo.getDesc(), todo.getTargetDate(), false);
    return "redirect:/list-todos";
  }

}
