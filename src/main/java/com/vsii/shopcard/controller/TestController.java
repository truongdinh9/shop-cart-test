package com.vsii.shopcard.controller;

import com.vsii.shopcard.model.Item;
import com.vsii.shopcard.model.Product;
import com.vsii.shopcard.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
//@SessionAttributes("cart")
public class TestController {
    @Autowired
    ProductRepo productRepo;

    @GetMapping
    ModelAndView ijjiji() {
        ModelAndView modelAndView = new ModelAndView("index");
        List<Product> products = productRepo.findAll();
        modelAndView.addObject("products", products);
        return modelAndView;
    }

    @GetMapping("cart")
    ModelAndView viewCart(HttpSession session) {
        List<Item> cart = (List<Item>) session.getAttribute("cart");
        Long amount = Long.valueOf(0);
        try {
            for (Item item : cart
            ) {
                amount += item.getProduct().getPrice() * item.getQuantity();

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        ModelAndView modelAndView = new ModelAndView("cart", "cart", cart);
        modelAndView.addObject("amount", amount);
        return modelAndView;
    }

    @GetMapping("cart/buy/{id}")
    ModelAndView addCard(HttpSession session, @PathVariable Long id, @RequestParam Long quantity) {
        if (quantity == 0) quantity = Long.valueOf(1);
        ModelAndView modelAndView = new ModelAndView("redirect:/cart");
        Product product = productRepo.findById(id).get();
        List<Item> cart = (List<Item>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<Item>();
        Item item = indexOfProduct(cart, product);
        if (item == null && quantity > 0) {
            cart.add(new Item(product, quantity));
        } else {
            if (quantity < 0 && item.getQuantity() < 1) quantity = Long.valueOf(0);
            item.setQuantity(item.getQuantity() + quantity);
        }
        session.setAttribute("cart", cart);
        return modelAndView;
    }

    @GetMapping("cart/addQuantity/{id}")
    ModelAndView redirect(@PathVariable Long id) {
        return new ModelAndView("redirect:/cart/buy/" + id + "?quantity=1");
    }

    @GetMapping("cart/subQuantity/{id}")
    ModelAndView sub(@PathVariable Long id) {
        return new ModelAndView("redirect:/cart/buy/" + id + "?quantity=-1");
    }

    @GetMapping("cart/delete")
    ModelAndView delete(HttpSession session) {
       session.setAttribute("cart",null);
        return new ModelAndView("redirect:/cart");
    }

    @GetMapping("cart/remove/{id}")
    ModelAndView remove(@PathVariable Long id, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/cart");
        try {
            Product product = productRepo.findById(id).get();
            List<Item> cart = (List<Item>) session.getAttribute("cart");
            Item item = indexOfProduct(cart, product);
            cart.remove(item);
            session.setAttribute("cart", cart);
        } catch (Exception e) {
            System.out.println(e);
        }
        return modelAndView;
    }
    @GetMapping("/view-product/{id}")
    ModelAndView view(@PathVariable Long id){
        return new ModelAndView("view","product",productRepo.findById(id).get());
    }

    private Item indexOfProduct(List<Item> cart, Product product) {
        try {
            for (Item item : cart) {
                if (item.getProduct().getId().equals(product.getId())) {
                    return item;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

}
