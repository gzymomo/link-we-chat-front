<script>
import { getDetail, add, update } from '@/api/intelligentShortLink'
import { typeDict, touchTypeDict } from './components/mixin'
export default {
  name: '',
  components: {
    PhonePreview: () => import('./components/PhonePreview.vue'),
    Add: () => import('./components/Add'),
  },
  data() {
    return {
      loading: false,
      currentActive: 0,
      form: {
        jumpType: 1,
        extensionType: 1,
        type: 0,
        avatar: '',
        qrCode: '',
      },

      typeDict,
      touchTypeDict,
      data: {},
    }
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {
    this.$route.query.id && this.getDetail()
    this.clipboard = new this.ClipboardJS('.copy-btn')
  },
  destroyed() {
    this.clipboard.destroy()
  },
  methods: {
    getDetail() {
      this.loading = true
      let id = this.$route.query.id
      getDetail(id)
        .then(({ data }) => {
          this.form = data
        })
        .catch((e) => {
          console.log(e)
        })
        .finally(() => {
          this.loading = false
        })
    },
    extensionTypeChange(val) {
      let touchTypeList = this.typeDict[val].touchType
      this.form.type = Object.keys(touchTypeList)[0]
    },
    goStep(type) {
      this.$refs.add.$refs.form.clearValidate()
    },
    submit() {
      this.$refs.add.$refs['form'].validate((validate) => {
        if (validate) {
          if ([1, 0].includes(this.form.type)) {
            this.form.touchType = this.form.type + 1
          } else {
            delete this.form.touchType
          }
          this.loading = true
          ;(this.form.id ? update : add)(this.form)
            .then((res) => {
              this.data = res.data
              this.currentActive++
            })
            .catch((e) => {
              console.log(e)
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    download() {
      let canvas = document.createElement('canvas')
      this.$refs.qrCode.setAttribute('crossOrigin', 'anonymous')
      canvas.getContext('2d').drawImage(this.$refs.qrCode, 0, 0)
      canvas.toBlob((blob) => {
        let url = URL.createObjectURL(blob)
        const link = document.createElement('a') // ??????a??????
        link.href = link.download = url
        link.click()
        link.remove()
        URL.revokeObjectURL(url)
      })
    },
  },
}
</script>

<template>
  <el-container v-loading="loading">
    <el-header class="g-card g-pad20 mb10" height="108px">
      <el-steps style="margin-top: 10px" :active="currentActive" align-center>
        <el-step title="????????????"></el-step>
        <el-step title="????????????"></el-step>
        <el-step title="????????????"></el-step>
      </el-steps>
    </el-header>

    <el-form
      v-show="currentActive == 0"
      label-suffix="???"
      label-width=""
      class="g-card g-pad20"
      style="padding-right: 18%">
      <el-form-item label="????????????">
        <el-radio-group v-model="form.jumpType">
          <el-radio-button :label="1">????????????</el-radio-button>
          <!-- manual
          :value="currentActive == 0"
           transition="normal" -->
          <el-tooltip class="item" effect="light" content="????????????" placement="top">
            <el-radio-button :label="2" disabled>????????????</el-radio-button>
          </el-tooltip>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="????????????">
        <el-radio-group v-model="form.extensionType" @change="extensionTypeChange">
          <el-radio-button v-for="(item, key) in typeDict" :key="key" :label="+key">
            {{ item.name }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="????????????">
        <el-radio-group v-model="form.type">
          <template v-for="(item, key) in typeDict">
            <div v-show="form.extensionType == key" :key="key">
              <el-radio-button v-for="(unit, unique) in item.touchType" :key="key + '-' + unique" :label="+unique">
                {{ unit.name }}
              </el-radio-button>
            </div>
          </template>
        </el-radio-group>
      </el-form-item>
      <div class="g-card g-pad20" style="background: #eee">
        <span>{{ touchTypeDict[form.type].tip }}</span>
      </div>
    </el-form>

    <div v-show="currentActive == 1" class="fxbw ais mt10" style="overflow: auto">
      <div class="g-card g-pad20 mr10" style="flex: auto">
        <Add ref="add" :form.sync="form" />
      </div>

      <PhonePreview :data="form" />
    </div>

    <div v-show="currentActive == 2" class="g-card g-pad20 ac">
      <i class="el-icon-success" style="font-size: 36px; color: #06c160; margin: 10px 0 20px"></i>
      <div style="">??????????????????</div>
      <div class="g-card g-pad20" style="background: #eee; width: 50%; margin: 30px auto">
        <span>{{ data.shortUrl }}</span>
      </div>
      <img ref="qrCode" style="width: 130px; height: 130px" :src="data.qrCode" fit="fit" crossOrigin="anonymous" />
      <div>
        <el-button type="text" class="copy-btn" :data-clipboard-text="data.shortUrl">????????????</el-button>
        <el-button type="text" @click="download()">???????????????</el-button>

        <!-- <div style="font-size: 12px; color: #aaa">
          ????????????????????????????????????????????????????????????????????????????????????????????????
          <a
            href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/qrcode-link/url-scheme/generateScheme.html">
            ??????>>
          </a>
        </div> -->
      </div>
    </div>

    <el-footer class="mt20 ar" style="padding: 0" height="">
      <template v-if="currentActive == 0">
        <el-button plain @click="$router.back()">??????</el-button>
        <el-button type="primary" @click="goStep(currentActive++)">?????????</el-button>
      </template>
      <template v-else-if="currentActive == 1">
        <el-button type="primary" @click="goStep(currentActive--)">?????????</el-button>
        <el-button type="primary" @click="submit()">????????????</el-button>
      </template>
      <template v-else-if="currentActive == 2">
        <el-button type="primary" @click="$router.back()">??????</el-button>
      </template>
    </el-footer>
  </el-container>
</template>

<style lang="scss" scoped></style>
