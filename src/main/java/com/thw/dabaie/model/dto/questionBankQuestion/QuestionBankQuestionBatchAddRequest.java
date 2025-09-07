package com.thw.dabaie.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建题库题目关联表请求
 *
 *
 *
 */
@Data
public class QuestionBankQuestionBatchAddRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}