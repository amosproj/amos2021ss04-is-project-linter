<template>
	<v-container>
		{{ project.name }}
		<v-list>
			<v-list-item v-for="res in project.results[0].checkResults" :key="res.id">
				<v-list-item-avatar>
					<v-icon class="yellow" dark>mdi-triangle</v-icon>
				</v-list-item-avatar>

				<v-list-item-content>
					<v-list-item-title v-text="res.checkName"></v-list-item-title>
					<v-list-item-subtitle>test</v-list-item-subtitle>
				</v-list-item-content>

				<v-list-item-action>
					<v-btn icon>
						<v-icon color="grey lighten-1">mdi-information</v-icon>
					</v-btn>
				</v-list-item-action>
			</v-list-item>
		</v-list>
	</v-container>
</template>

<script>
import ax from "@/api";

export default {
	name: "Result",
	data() {
		return {
			project: {},
		};
	},
	mounted() {
		ax.get(`/project/${this.$route.params.id}`)
			.then((res) => {
				this.project = res.data;
			})
			.catch((err) => {
				console.log(err);
			});
	},
};
</script>
