package com.jd.drools.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jack.common.util.DateUtils;
import org.junit.Test;

public class RuleTest {
    @Test
    public void testPointRule(String input) {
        PointRuleEngine pointRuleEngine = new PointRuleEngineImpl();
        while (true) {
            InputStream is = System.in;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            // String input = null;
            // try {
            // input = br.readLine();
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
            System.out.println("请输入命令：");
            if (null != input && "s".equals(input)) {
                System.out.println("初始化规则引擎…");
                pointRuleEngine.initEngine();
                System.out.println("初始化规则引擎结束.");
            } else if ("e".equals(input)) {
                final PointDomain pointDomain = new PointDomain();
                System.out.println("初始化规则引擎…");
                pointRuleEngine.initEngine();
                System.out.println("初始化规则引擎结束.");
                long startMillis = System.currentTimeMillis();
                pointDomain.setUserName("hello kity");
                pointDomain.setBackMondy(100d);
                pointDomain.setBuyMoney(500d);
                pointDomain.setBackNums(1);
                pointDomain.setBuyNums(5);
                pointDomain.setBillThisMonth(5);
                pointDomain.setBirthDay(true);
                pointDomain.setPoint(0l);

                Customer customer = new Customer();
                customer.setCustName("严文");
                customer.setSex("0");
                customer.setIdentityType("0");
                customer.setIdentityNo("34010119760104882X");
                customer.setBindMobile("18604103630");
                customer.setChannelId("TTTNET01");
                pointDomain.setCustomer(customer);

                List<Order> orders = new ArrayList<Order>();
                Order order = new Order();
                // 5000.00 02 TTTNET01 2020/3/20 14:07:15 F11800 0 0 1 10
                // 10000.00 02 TTTNET01 2020/3/20 14:12:41 F11800 7 2 0 1 10
                order.setBalance(BigDecimal.valueOf(5000));
                order.setBusiflag("02");
                order.setChannelId("TTTNET01");
                try {
                    order.setCreateTime(DateUtils.parseDate("2020/3/20 14:07:15", "yyyy/MM/dd HH:mm:ss"));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                order.setFundCode("F11800");
                order.setPayStatus(null);
                order.setPayType("0");
                order.setProfitclass("0");
                order.setReserve2("1");
                order.setStatus("10");
                orders.add(order);
                order=new Order();
                order.setBalance(BigDecimal.valueOf(10000));
                order.setBusiflag("02");
                order.setChannelId("TTTNET01");
                try {
                    order.setCreateTime(DateUtils.parseDate("2020/3/20 14:12:41", "yyyy/MM/dd HH:mm:ss"));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                order.setFundCode("F11800");
                order.setPayStatus("7");
                order.setPayType("2");
                order.setProfitclass("0");
                order.setReserve2("1");
                order.setStatus("10");
                orders.add(order);
                pointDomain.setOrders(orders);

                Customer customer2 = new Customer();
                customer2.setCustName("严文");
                customer2.setSex("0");
                customer2.setIdentityType("0");
                customer2.setIdentityNo("34010119760104882X");
                customer2.setBindMobile("18604103630");
                customer2.setChannelId("TTTNET01");
                pointRuleEngine.executeRuleEngine(pointDomain,customer,orders,customer2);
                System.out.println(String.format("执行时间：%sms", System.currentTimeMillis()-startMillis));
                System.out.println("执行完毕BillThisMonth：" + pointDomain.getBillThisMonth());
                System.out.println("执行完毕BuyMoney：" + pointDomain.getBuyMoney());
                System.out.println("执行完毕BuyNums：" + pointDomain.getBuyNums());
                System.out.println("执行完毕规则引擎决定发送积分：" + pointDomain.getPoint());
            } else if ("r".equals(input)) {
                System.out.println("刷新规则文件…");
                pointRuleEngine.refreshEnginRule();
                System.out.println("刷新规则文件结束.");
            }
            break;
        }
    }
    public static void main(String[] args) {
        RuleTest test=new RuleTest();
        test.testPointRule("e");
    }
}