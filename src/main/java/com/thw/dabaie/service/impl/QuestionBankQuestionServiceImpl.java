package com.thw.dabaie.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thw.dabaie.common.ErrorCode;
import com.thw.dabaie.constant.CommonConstant;
import com.thw.dabaie.exception.BusinessException;
import com.thw.dabaie.exception.ThrowUtils;
import com.thw.dabaie.mapper.QuestionBankQuestionMapper;
import com.thw.dabaie.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.thw.dabaie.model.entity.Question;
import com.thw.dabaie.model.entity.QuestionBank;
import com.thw.dabaie.model.entity.QuestionBankQuestion;
import com.thw.dabaie.model.entity.User;
import com.thw.dabaie.model.vo.QuestionBankQuestionVO;
import com.thw.dabaie.model.vo.UserVO;
import com.thw.dabaie.service.QuestionBankQuestionService;
import com.thw.dabaie.service.QuestionBankService;
import com.thw.dabaie.service.QuestionService;
import com.thw.dabaie.service.UserService;
import com.thw.dabaie.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 题库题目关联表服务实现
 *
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private QuestionService questionService;

    @Resource
    private QuestionBankService questionBankService;

    /**
     * 校验数据
     *对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
        // 题目和题库必须存在
        Long questionId = questionBankQuestion.getQuestionId();
        if (questionId != null) {
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        Long questionBankId = questionBankQuestion.getQuestionBankId();
        if (questionBankId != null) {
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        }
    }


    /**
     * 获取查询条件
     *
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索

        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目关联表封装
     *
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long questionBankQuestionId = questionBankQuestion.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//
//        }
        // endregion

        return questionBankQuestionVO;
    }

    /**
     * 分页获取题库题目关联表封装
     *
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream().map(questionBankQuestion -> {
            return QuestionBankQuestionVO.objToVo(questionBankQuestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> questionBankQuestionIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> questionBankQuestionIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {

        }
        // 填充信息
        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    /**
     * 批量添加题库题目
     *
     */
    @Override
    public void batchAddQuestionsToBank(List<Long> questionIdList, long questionBankId, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表不能为空");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库id不能小于0");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 检查题目 id 是否存在  如果为了优化 sql，尽量不要用 select * 拼出来的就类似于select id from question where id in (1,2,3)
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper = Wrappers.lambdaQuery(Question.class)
                        .select(Question::getId)
                        .in(Question::getId, questionIdList);
        // 避免生成对象，直接将对象转 Long，节省内存
        List<Long> validQuestionIdList = questionService.listObjs(questionLambdaQueryWrapper ,object -> (Long) object );
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法的题目 id 为空");
        // 查询哪些题目在题库中
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                .in(QuestionBankQuestion::getQuestionId, validQuestionIdList);

        List<QuestionBankQuestion> existQuestionList = this.list(lambdaQueryWrapper);
        // 遍历出已存在于题库中的题目 ID，使用 Set 集合天然去重
        Set<Long> existQuestionIdSet = existQuestionList.stream().map(QuestionBankQuestion::getQuestionId).collect(Collectors.toSet());

        // 已经存在于题库中的题目无需再次添加
        validQuestionIdList  = validQuestionIdList.stream().filter(questionId -> !existQuestionIdSet.contains(questionId)).collect(Collectors.toList());

        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "所有题目都已存在于题库中");
        // 检查题目 id 是否存在
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        // 分批处理 1000条 防止长事务
        int batchSize = 1000;
        int totalQuestionListSize = validQuestionIdList.size();
        for (int i = 0; i < totalQuestionListSize;i += batchSize) {
            // 生成每批次的数据
            List<Long> subList = validQuestionIdList.subList(i, Math.min(i + batchSize, totalQuestionListSize));
            List<QuestionBankQuestion> questionBankQuestionList = subList.stream().map(questionId -> {
                QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                questionBankQuestion.setQuestionBankId(questionBankId);
                questionBankQuestion.setQuestionId(questionId);
                questionBankQuestion.setUserId(loginUser.getId());
                return questionBankQuestion;
            }).collect(Collectors.toList());
            // 获取代理 使用 AOP 注解 需要开启spring.aop.proxy-target-class: true 启动类
            QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionServiceImpl) AopContext.currentProxy();
            // 使用事务 分批调用事务 要使用代理类 不能直接调用
            questionBankQuestionService.batchRemoveQuestionToBankInner(questionBankQuestionList);
        }


    }
    /**
     * 批量添加题库题目（事务，内部使用）
     *
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchRemoveQuestionToBankInner(List<QuestionBankQuestion> questionBankQuestionList) {
        // 性能太低下，不用 for 循环
//        for (QuestionBankQuestion questionBankQuestion : questionBankQuestionList) {
//            Long questionBankId = questionBankQuestion.getQuestionBankId();
//            Long questionId = questionBankQuestion.getQuestionId();
//            try {
//                boolean result = this.save(questionBankQuestion);
//                if (!result) {
//                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
//                }
//            } catch (DataIntegrityViolationException e) {
//                log.error("数据库唯一键冲突或违反其他完整性约束，题目 id: {}, 题库 id: {}, 错误信息: {}",
//                        questionId, questionBankId, e.getMessage());
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在于该题库，无法重复添加");
//            } catch (DataAccessException e) {
//                log.error("数据库连接问题、事务问题等导致操作失败，题目 id: {}, 题库 id: {}, 错误信息: {}",
//                        questionId, questionBankId, e.getMessage());
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
//            } catch (Exception e) {
//                // 捕获其他异常，做通用处理
//                log.error("添加题目到题库时发生未知错误，题目 id: {}, 题库 id: {}, 错误信息: {}",
//                        questionId, questionBankId, e.getMessage());
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
//            }
//        }
        boolean b = this.saveBatch(questionBankQuestionList);
        ThrowUtils.throwIf(!b,ErrorCode.OPERATION_ERROR,"向题库添加题目失败");
    }



    /**
     * 批量从题库移除题目
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestionToBank(List<Long> questionIdList, long questionBankId) {
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR,"题目列表不能为空");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR,"题库id不能小于0");

        for (Long questionId : questionIdList) {
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            boolean result = this.remove(lambdaQueryWrapper);
            ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"从题库中移除题目失败");
        }

    }

}
