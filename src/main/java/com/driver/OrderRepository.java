package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository() {
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order) {
        // your code here
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId) {
        // your code here
        // create a new partner with given partnerId and save it
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, partner);
    }

    public void saveOrderPartnerMap(String orderId, String partnerId) {
        if (orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)) {
            // your code here
            //add order to given partner's order list
            partnerToOrderMap.putIfAbsent(partnerId, new HashSet<String>());
            partnerToOrderMap.get(partnerId).add(orderId);
            //increase order count of partner
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);
            //assign partner to this order
            orderToPartnerMap.put(orderId, partnerId);
        }
    }

    public Order findOrderById(String orderId) {
        // your code here
        return orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId) {
        // your code here
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId) {
        // your code here
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        return partner.getNumberOfOrders();
    }

    public List<String> findOrdersByPartnerId(String partnerId) {
        // your code here
        HashSet<String> ordersSet = partnerToOrderMap.getOrDefault(partnerId, new HashSet<String>());
        return new ArrayList<>(ordersSet);
    }

    public List<String> findAllOrders() {
        // your code here
        // return list of all orders
        List<String> list = new ArrayList<>();
        for (var e : orderMap.entrySet()) list.add(e.getKey());

        return list;
    }

    public void deletePartner(String partnerId) {
        // your code here
        // delete partner by ID
        partnerMap.remove(partnerId);
    }

    public void deleteOrder(String orderId) {
        // your code here
        // delete order by ID
        orderMap.remove(orderId);
    }

    public Integer findCountOfUnassignedOrders() {
        // your code here
        int count = 0;
        for (String order : orderMap.keySet()) {
            if (!orderToPartnerMap.containsKey(order)) count++;
        }

        return count;
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId) {
        // your code here
        int givenTime = convertDeliveryTimeToMinutes(timeString);
        int count = 0;

        if (partnerToOrderMap.containsKey(partnerId)) {
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                Order order = orderMap.get(orderId);
                if (order.getDeliveryTime() > givenTime) {
                    count++;
                }
            }
        }
        return count;
    }

    private int convertDeliveryTimeToMinutes(String timeString) {
        String[] parts = timeString.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId) {
        // your code here
        // code should return string in format HH:MM
        int latestTime = -1;

        if (partnerToOrderMap.containsKey(partnerId)) {
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                Order order = orderMap.get(orderId);
                if (order.getDeliveryTime() > latestTime) {
                    latestTime = order.getDeliveryTime();
                }
            }
        }

        if (latestTime == -1) {
            return null; // or return "00:00" or any other indication of no deliveries
        }

        return convertMinutesToTimeString(latestTime);
    }

    private String convertMinutesToTimeString(int minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        return String.format("%02d:%02d", hours, remainingMinutes);
    }
}