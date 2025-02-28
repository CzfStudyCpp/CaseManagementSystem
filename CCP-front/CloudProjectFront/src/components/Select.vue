<template>
  <div class="select-checked">
    <el-select
        :value="selected"
        multiple
        placeholder="请选择"
        :popper-append-to-body="false"
    >
      <el-option :value="''" label="全部" class="multiple">
        <el-checkbox v-model="optionsAll" @change="handleoptionsAllChange">
          全部
        </el-checkbox>
      </el-option>
      <el-option
          class="multiple"
          :value="key"
          :label="item"
          v-for="(item, key) in optionsData"
          :key="key"
      >
        <el-checkbox
            :value="selectedOptions.includes(key)"
            @change="handleTaskItemChange(key)"
        >
          {{ item }}
        </el-checkbox>
      </el-option>
    </el-select>
  </div>
</template>

<script>
export default {
  name: 'Select',
  components: {},
  props: {
    options: {
      type: Object
    }
  },
  data() {
    return {
      optionsData: {},
      optionsAll: true,
      selectedOptions: [],
    }
  },
  watch: {
    options: {
      handler(newVal) {
        console.log(newVal)
        this.optionsData = newVal
        this.selectedOptions = Object.keys(newVal)
      },
      immediate: true, // 该值默认是false，在进入页面时，第一次绑定值，不会立刻执行监听，只有数据发生改变才会执行handler中的操作
      // deep: true, // deep 深度
    },
  },
  computed: {
    selected() {
      if (
          this.selectedOptions.length === Object.keys(this.optionsData).length
      ) {
        return ['']
      } else {
        return this.selectedOptions
      }
    }
  },
  methods: {
    handleoptionsAllChange(isAll) {
      if (isAll) {
        this.selectedOptions = Object.keys(this.optionsData)
      } else {
        this.selectedOptions = []
      }
    },
    handleTaskItemChange(key) {
      if (this.selectedOptions.includes(key)) {
        this.selectedOptions.splice(this.selectedOptions.indexOf(key), 1)
      } else {
        this.selectedOptions.push(key)
      }
      this.optionsAll =
          this.selectedOptions.length === Object.keys(this.optionsData).length
    }
  }
}
</script>

<style lang="scss">
.select-checked {
  .el-select-dropdown.is-multiple .el-select-dropdown__item.selected::after {
    content: '';
  }
  .el-checkbox {
    width: 100%;
    padding: 0 30px;
    .el-checkbox__label {
      margin-left: 20px;
    }
  }
  .el-select-dropdown__item {
    padding: 0;
  }
  .el-tag__close,
  .el-icon-close {
    display: none;
  }
  .el-tag.el-tag--info {
    background: transparent;
    border: 0;
  }

  .el-select {
    .el-select__tags {
      flex-wrap: nowrap;
      overflow: hidden;
    }
    .el-tag {
      background-color: #fff;
      border: none;
      color: #606266;
      font-size: 13px;
      padding-right: 0;
      & ~ .el-tag {
        margin-left: 0;
      }
      &:not(:last-child)::after {
        content: ',';
      }
    }
  }
}
</style>

