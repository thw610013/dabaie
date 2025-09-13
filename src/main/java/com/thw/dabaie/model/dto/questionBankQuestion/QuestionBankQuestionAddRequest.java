package com.thw.dabaie.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建题库题目关联表请求
 *
 
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionBankQuestionAddRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}