package com.thw.dabaie.mapper;

import com.thw.dabaie.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author tianhaowen
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2025-07-19 12:03:04
* @Entity com.thw.dabaie.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 查询题目列表（包括已经删除的数据）
     * @param minUpdateTime
     * @return
     */
    @Select("select * from question where updatetime >= #{minUpdateTime}")
    List<Question> listQuestionWithDelete(Date minUpdateTime);



}




