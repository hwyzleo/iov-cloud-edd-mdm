#!/bin/bash

# 重命名Java文件
rename_file() {
    local old_path="$1"
    local new_path="$2"
    
    if [ -f "$old_path" ]; then
        mv "$old_path" "$new_path"
        echo "Renamed: $old_path -> $new_path"
    else
        echo "File not found: $old_path"
    fi
}

# 更新文件内容
update_content() {
    local file="$1"
    if [ -f "$file" ]; then
        sed -i '' 's/Series/CarLine/g' "$file"
        sed -i '' 's/series/carLine/g' "$file"
        echo "Updated content: $file"
    fi
}

# API模块文件
rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/service/SeriesService.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/service/CarLineService.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/fallback/SeriesServiceFallbackFactory.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/fallback/CarLineServiceFallbackFactory.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesResponse.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/CarLineResponse.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesPageResponse.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/CarLinePageResponse.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesHistoryResponse.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/CarLineHistoryResponse.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesHistoryPageResponse.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/CarLineHistoryPageResponse.java"

# Service模块文件
rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/mpt/MptSeriesController.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/mpt/MptCarLineController.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/service/ServiceSeriesController.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/service/ServiceCarLineController.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/upstream/UpstreamSeriesController.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/upstream/UpstreamCarLineController.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/assembler/SeriesAssembler.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/assembler/CarLineAssembler.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/service/SeriesAppService.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/service/CarLineAppService.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/aggregate/Series.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/aggregate/CarLine.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/entity/SeriesHistory.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/entity/CarLineHistory.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/SeriesType.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/CarLineType.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/SeriesStatus.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/CarLineStatus.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/SeriesCreatedEvent.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/CarLineCreatedEvent.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/SeriesUpdatedEvent.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/CarLineUpdatedEvent.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/SeriesDeactivatedEvent.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/CarLineDeactivatedEvent.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/repository/SeriesRepository.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/repository/CarLineRepository.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/repository/SeriesRepositoryImpl.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/repository/CarLineRepositoryImpl.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/result/SeriesDto.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/result/CarLineDto.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/result/SeriesHistoryDto.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/result/CarLineHistoryDto.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/cmd/SeriesCreateCmd.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/cmd/CarLineCreateCmd.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/cmd/SeriesUpdateCmd.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/cmd/CarLineUpdateCmd.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/query/SeriesQuery.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/query/CarLineQuery.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/po/SeriesPo.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/po/CarLinePo.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/po/SeriesHistoryPo.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/po/CarLineHistoryPo.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/mapper/SeriesMapper.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/mapper/CarLineMapper.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/mapper/SeriesHistoryMapper.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/mapper/CarLineHistoryMapper.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/converter/SeriesConverter.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/converter/CarLineConverter.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/converter/SeriesHistoryConverter.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/converter/CarLineHistoryConverter.java"

# 更新所有Java文件内容
find . -name "*.java" -type f -exec grep -l "Series" {} \; | while read file; do
    update_content "$file"
done

echo "Rename completed!"
