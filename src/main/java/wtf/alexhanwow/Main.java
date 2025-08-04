package wtf.alexhanwow;


import snw.jkook.command.JKookCommand;
import snw.jkook.plugin.BasePlugin;
import wtf.alexhanwow.Costum.Goods;
import wtf.alexhanwow.pay.pay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static wtf.alexhanwow.pay.pay.MERCHANT_ID;
import static wtf.alexhanwow.pay.pay.createPayment;


public class Main extends BasePlugin {
    public static boolean open = true;
    public static String Code = null;

    @Override
    public void onEnable() {
        getLogger().info("  ___        __ _   ___      _ _         ");
        getLogger().info(" |   \\ __ _ / _| |_/ __| ___| | |___ _ _ ");
        getLogger().info(" | |) / _` |  _|  _\\__ \\/ -_) | / -_) '_|");
        getLogger().info(" |___/\\__,_|_|  \\__|___/\\___|_|_\\___|_|  ");
        getLogger().info("               Made by AlexhanWOW , MIT License");
        getLogger().info("                               qq:439264225");

        if (open) {
            Code = CodeGen.Random(52, open);
            getLogger().info("Code:" + Code);
        } else {
            getLogger().info("密钥通道已关闭");
        }

        //管理员设置
        new JKookCommand("setAdmin", "/").executesUser((sender, arguments, message) -> {
            try {
                String test = arguments[0].toString();
            } catch (ArrayIndexOutOfBoundsException e) {
                message.reply("请输入有效的密钥");
            }
            String typeCode = arguments[0].toString();
            if (Objects.equals(Code, typeCode) && open) {

                String password = CodeGen.Random(10, open);
                message.reply("正在将用户：" + sender.getId() + "(" + sender.getName() + ")设置为总管理员,密码为" + password);
                int i = setAdmin.set(sender.getId(), password);
                if (i == 1) message.reply("设置成功");
                else message.reply("设置失败，请联系开发者");

            } else if (!Objects.equals(Code, typeCode)) {
                message.reply("设置失败，原因：密钥错误");
            } else if (!open) {
                message.reply("设置失败，原因：密钥通道关闭");
            }
        }).register(this);

        //


        //用户查询指令
        new JKookCommand("list", "/").executesUser((sender, arguments, messages) -> {
            Map<String, Integer> Fuck = Goods.count();
            if (Fuck != null) {
                System.out.println("各列非空值统计结果:");
                for (Map.Entry<String, Integer> entry : Fuck.entrySet()) {
                    messages.reply(entry.getKey() + " | " + entry.getValue());
                }
            } else {
                messages.reply("查询失败");
            }
        }).register(this);

        //添加商品
        new JKookCommand("addGoods", "/").executesUser((sender, arguments, messages) -> {
            if (Goods.isAdmin(sender.getId())) {
                int code = Goods.addGood(arguments[0].toString(), arguments[1].toString());
                if (code == 1) {
                    messages.reply("成功添加新的商品");
                } else {
                    messages.reply("添加商品失败，请联系开发者");
                }
            } else {
                messages.reply("操作失败，你并没有管理员权限");
            }
        }).register(this);

        //添加商品名称
        new JKookCommand("addCat", "/").executesUser((sender, arguments, messages) -> {
            if (Goods.isAdmin(sender.getId())) {
                int code = Goods.addCat(arguments[0].toString(), arguments[1].toString(), null);
                if (code == 1) {
                    messages.reply("成功添加新的商品名称");
                } else {
                    messages.reply("添加商品名称失败，请联系开发者");
                }
            } else {
                messages.reply("操作失败，你并没有管理员权限");
            }
        }).addSubcommand(new JKookCommand("Details").executesUser((sender, arguments, messages) -> {
            if (Goods.isAdmin(sender.getId())) {
                int code = Goods.addCat(arguments[0].toString(), arguments[1].toString(), arguments[2].toString());
                if (code == 1) {
                    messages.reply("成功添加新的商品名称");
                } else {
                    messages.reply("添加商品名称失败，请联系开发者");
                }
            } else {
                messages.reply("操作失败，你并没有管理员权限");
            }
        })).register(this);


        //改变商品金额
        new JKookCommand("changeAmount", "/").executesUser((sender, arguments, messages) -> {
            if (Goods.isAdmin(sender.getId())) {
                if (Goods.changeAmount(arguments[0].toString(), Double.parseDouble(arguments[1].toString())) == 1) {
                    messages.reply("修改金额成功");
                } else {
                    messages.reply("修改金额失败，请联系开发者");
                }
            } else {
                messages.reply("操作失败，你并没有管理员权限");
            }
        }).register(this);

        //删除商品
        new JKookCommand("deleteGoods", "/").executesUser((sender, arguments, messages) -> {
            if (Goods.isAdmin(sender.getId())) {
                int a = Goods.deleteGoods(arguments[0].toString(),arguments[1].toString());
                if (a == 1){
                    messages.reply("删除成功");
                }else {
                    messages.reply("删除失败");
                }

            } else {
                messages.reply("操作失败，你并没有管理员权限");
            }
        }).register(this);

        //删除商品名称
        new JKookCommand("deleteCat", "/").executesUser((sender, arguments, message) -> {
            if (Goods.isAdmin(sender.getId())) {
                message.reply("删除商品名称，会导致你的商品也被删除，如果你想继续操作，请输入/Confirm,如果放弃了操作请输入/Cancel");
                String s = Goods.deleteCat(arguments[0].toString());
                if (s != "OK") {
                    message.reply("操作失败,原因" + s);
                }
            } else {
                message.reply("操作失败，你并没有管理员权限");
            }
        }).register(this);

        //确认
        new JKookCommand("Confirm", "/").executesUser((sender, arguments, message) -> {
            String a = Goods.Confirm();
            if (a == "Success"){
                message.reply("删除成功");
            }else {
                message.reply("出现错误，原因："+ a);
            }
        }).register(this);

        /*
         *
         * 这里我建议各位自己写，我这里写的像一坨屎。我可能后续会更新，但这里目前不能用。
         * 我这里使用deepseek生成得到的
         * 但这里的函数都可以用，只有支付逻辑不可用
         *
         * */
        new JKookCommand("buy", "/").executesUser((sender, arguments, messages) -> {
            String payURL = createPayment(100.00);
            Double amount = Goods.getAmount(arguments[0].toString(), arguments[1].toString());

            // 示例：模拟回调处理
            Map<String, String> mockCallback = new HashMap<>();
            mockCallback.put("merchant_id", MERCHANT_ID);
            //支付ID
            String a = pay.generate(pay.OrderType.OR);
            mockCallback.put("order_id", a);
            //金额
            mockCallback.put("amount", amount.toString());
            //状态
            mockCallback.put("status", "success");

            if (pay.handlePaymentCallback(mockCallback) == "SUCCESS") {
                messages.reply("支付成功");
            }
        }).register(this);


        //密钥通道
        new JKookCommand("Admin", "/").executesUser((sender, auguments, messages) -> {
            if (Goods.isAdmin(sender.getId())) {
                if (open == true) {

                    open = false;
                    messages.reply("管理员注册通道已关闭");
                } else {
                    open = true;
                    messages.reply("管理员注册通道已开启");
                }
            } else {
                messages.reply("操作失败，你并没有管理员权限");
            }
        }).register(this);

        //修改密钥
        new JKookCommand("keyChange").executesUser((sender, auguments, messages) -> {
            if (Goods.isAdmin(sender.getId())) {
                Code = CodeGen.Random(52, open);
                getLogger().warn("密钥已修改为:" + Code);

            } else {
                messages.reply("操作失败，你并没有管理员权限");
            }
        }).register(this);
    }

    @Override
    public void onDisable() {
        getLogger().warn("感谢使用DaftSeller");
    }
}