package com.fanchenyi.diet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fanchenyi.diet.mapper.DietRecordMapper;
import com.fanchenyi.diet.mapper.FoodMapper;
import com.fanchenyi.diet.mapper.SysUserMapper;
import com.fanchenyi.diet.model.entity.DietRecord;
import com.fanchenyi.diet.model.entity.Food;
import com.fanchenyi.diet.model.entity.SysUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO 层单元测试
 * 验证数据库连接、MyBatis-Plus 自动填充、CRUD 操作
 */
@SpringBootTest
public class MapperTest {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private DietRecordMapper dietRecordMapper;

    @Test
    @DisplayName("测试用户插入与自动填充")
    public void testInsertUser() {
        // 1. 准备数据 (不设置 id 和 createTime/updateTime，验证自动生成)
        SysUser user = new SysUser();
        // 为了防止唯一索引冲突，使用时间戳生成用户名
        String username = "test_user_" + System.currentTimeMillis();
        user.setUsername(username);
        user.setPasswordHash("e10adc3949ba59abbe56e057f20f883e"); // 假设这是 "123456" 的 MD5
        user.setGender(1); // 1:男
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // 2. 执行插入
        int result = sysUserMapper.insert(user);

        // 3. 验证结果
        // 断言影响行数为 1
        Assertions.assertEquals(1, result);
        // 断言 ID 已回填（MyBatis-Plus 主键回填特性）
        Assertions.assertNotNull(user.getId());
        System.out.println("插入用户成功，生成ID: " + user.getId());

        // 4. 再次查询以验证 create_time 是否自动填充
        SysUser queryUser = sysUserMapper.selectById(user.getId());
        Assertions.assertNotNull(queryUser.getCreateTime(), "创建时间应该被自动填充");
        Assertions.assertNotNull(queryUser.getUpdateTime(), "更新时间应该被自动填充");
        Assertions.assertEquals(0, queryUser.getDeleted(), "默认逻辑删除值应为 0");
    }

    @Test
    @DisplayName("测试逻辑删除")
    public void testLogicDelete() {
        // 1. 先插入一个用户
        SysUser user = new SysUser();
        user.setUsername("delete_test_" + System.currentTimeMillis());
        user.setPasswordHash("123456");
        user.setGender(2);
        sysUserMapper.insert(user);
        Long id = user.getId();

        // 2. 执行删除 (MyBatis-Plus 会自动将 delete 转换为 update is_deleted = 1)
        sysUserMapper.deleteById(id);

        // 3. 验证查询 (默认查询会自动过滤 is_deleted = 1 的数据)
        SysUser deletedUser = sysUserMapper.selectById(id);
        Assertions.assertNull(deletedUser, "逻辑删除后的数据不应被常规查询查出");

        // 如果你想在控制台确认数据库里数据还在（只是 is_deleted 变了），可以去数据库客户端看一眼
    }

    @Test
    @DisplayName("测试关联业务：添加食物与记录")
    public void testDietFlow() {
        // 1. 插入食物
        Food food = new Food();
        food.setFoodName("香蕉_" + System.currentTimeMillis());
        food.setCalories(new BigDecimal("89.0"));
        food.setProtein(new BigDecimal("1.1"));
        food.setFat(new BigDecimal("0.3"));
        food.setCarbohydrate(new BigDecimal("22.8"));
        foodMapper.insert(food);
        Assertions.assertNotNull(food.getId());

        // 2. 插入用户
        SysUser user = new SysUser();
        user.setUsername("diet_user_" + System.currentTimeMillis());
        user.setPasswordHash("123456");
        user.setGender(1);
        sysUserMapper.insert(user);

        // 3. 插入饮食记录
        DietRecord record = new DietRecord();
        record.setUserId(user.getId());
        record.setFoodId(food.getId());
        record.setFoodNameSnapshot(food.getFoodName()); // 记录当时的食物名称
        record.setMealType(0); // 早餐
        record.setQuantity(new BigDecimal("100")); // 100g
        record.setRecordDate(LocalDate.now());

        int rows = dietRecordMapper.insert(record);
        Assertions.assertEquals(1, rows);

        // 4. 简单查询验证
        List<DietRecord> records = dietRecordMapper.selectList(
                new QueryWrapper<DietRecord>().eq("user_id", user.getId())
        );
        Assertions.assertFalse(records.isEmpty());
        Assertions.assertEquals(food.getFoodName(), records.get(0).getFoodNameSnapshot());
    }
}