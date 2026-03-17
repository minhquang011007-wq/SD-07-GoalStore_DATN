<script setup lang="ts">
const banners = [
  { title: "Clothing Collections 2030", image: "/storefront/banner/banner-1.jpg", align: "lg:col-span-7 lg:ml-auto" },
  { title: "Accessories", image: "/storefront/banner/banner-2.jpg", align: "lg:col-span-5" },
  { title: "Shoes Spring 2030", image: "/storefront/banner/banner-3.jpg", align: "lg:col-span-7" },
]
</script>

<template>
  <section class="bg-white py-20">
    <div class="mx-auto grid max-w-7xl gap-8 px-4 lg:grid-cols-12">
      <article v-for="(banner, index) in banners" :key="banner.title" class="group overflow-hidden rounded-[28px] bg-[#f8f5f2] shadow-sm" :class="banner.align">
        <div class="grid gap-6 p-6 md:grid-cols-2 md:items-center md:p-8">
          <img :src="banner.image" :alt="banner.title" class="h-72 w-full rounded-3xl object-cover transition duration-500 group-hover:scale-[1.03]" />
          <div>
            <p class="mb-3 text-sm uppercase tracking-[0.3em] text-neutral-400">Collection {{ index + 1 }}</p>
            <h3 class="mb-5 text-3xl font-semibold text-black">{{ banner.title }}</h3>
            <router-link to="/shop" class="inline-flex items-center gap-2 border-b border-black pb-1 text-sm font-semibold uppercase tracking-[0.18em] text-black">
              Shop now
            </router-link>
          </div>
        </div>
      </article>
    </div>
  </section>
</template>
