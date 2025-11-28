package com.neusoft.neu23.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.neu23.entity.Test;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TestMapper extends BaseMapper<Test> {
    
    @Select("SELECT COUNT(*) FROM test WHERE deptno = #{deptno} AND dname = #{dname}")
    int countByDeptnoAndDname(@Param("deptno") String deptno, @Param("dname") String dname);
}