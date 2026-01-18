package com.tt.cm.service.impl;

import com.tt.cm.domain.MovieCategory;
import com.tt.cm.domain.MovieToCategory;
import com.tt.cm.mapper.MovieCategoryMapper;
import com.tt.cm.service.MovieCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieCategoryServiceImpl implements MovieCategoryService {

    @Resource
    private MovieCategoryMapper movieCategoryMapper;

    @Override
    public List<MovieCategory> findAllCategorys() {
        return movieCategoryMapper.findAllCategorys();
    }

    @Override
    public MovieCategory findCategoryById(Long id) {
        return movieCategoryMapper.findCategoryById(id);
    }

    @Override
    public int addCategory(MovieCategory movieCategory) {
        return movieCategoryMapper.addCategory(movieCategory);
    }

    @Override
    public int updateCategory(MovieCategory movieCategory) {
        return movieCategoryMapper.updateCategory(movieCategory);
    }

    @Override
    public int deleteCategory(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += movieCategoryMapper.deleteCategory(id);
        }
        return rows;
    }


    @Override
    public int addMovieToCategory(MovieToCategory movieToCategory) {
        return movieCategoryMapper.addMovieToCategory(movieToCategory);
    }

    @Override
    public int deleteMovieToCategory(MovieToCategory movieToCategory) {
        return movieCategoryMapper.deleteMovieToCategory(movieToCategory);
    }

    // 对外提供统计方法
    @Override
    public Map<String, Integer> countUserMovieTypePreference(Long userId) {
        // 调用Mapper获取统计结果，无数据时返回空Map避免前端报错
//        List<Map<String, Object>> resultList = sysMovieCategoryMapper.countUserMovieTypePreference(userId);
//      Map<String, Integer> resultMap = new HashMap<>();
//
//        for (Map<String, Object> item : resultList) {
//            String type = (String) item.get("type");
//            Integer count = ((Number) item.get("count")).intValue();
//            resultMap.put(type, count);
//        }
//
//        return resultMap;
        // 1. 获取原始数据（所有类型的统计结果）
        List<Map<String, Object>> originalData = movieCategoryMapper.countUserMovieTypePreference(userId);

        // 2. 转换为有序映射并按次数排序
        List<Map.Entry<String, Integer>> sortedEntries = originalData.stream()
                .map(item -> new AbstractMap.SimpleEntry<>(
                        item.get("typeName").toString(),
                        Integer.parseInt(item.get("count").toString())
                ))
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        // 3. 处理前三名和"其他"类别
        Map<String, Integer> result = new LinkedHashMap<>();
        int otherCount = 0;

        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, Integer> entry = sortedEntries.get(i);
            if (i < 3) {
                // 前三名直接添加
                result.put(entry.getKey(), entry.getValue());
            } else {
                // 其余类别累加至"其他"
                otherCount += entry.getValue();
            }
        }
        // 4. 如果有其他类别，添加"其他"项
        if (sortedEntries.size() > 3) {
            result.put("其他", otherCount);
        }

        return result;
    }
}