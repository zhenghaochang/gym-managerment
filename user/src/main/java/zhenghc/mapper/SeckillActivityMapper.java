package zhenghc.mapper;

import org.apache.ibatis.annotations.Mapper;
import zhenghc.entity.SeckillActivity;

import java.util.List;

@Mapper
public interface SeckillActivityMapper {


    void insert(SeckillActivity param);

    List<SeckillActivity> selectAll();

    SeckillActivity selectById(Long id);

    void updateStatus(Long id, Integer status);

    void delete(Long id);

    void decreaseStock(Long activityId);

    void increaseStock(Long activityId);
}
