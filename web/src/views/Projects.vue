<template>
	<div>
		<v-row>
			<v-col v-for="proj in projects" :key="proj.id" cols="3">
				<v-card class="mx-auto" max-width="344" outlined height="250px">
					<v-card-title>{{ proj.name }}</v-card-title>
					<v-card-subtitle>
						<a :href="proj.url">{{ proj.url }}</a>
					</v-card-subtitle>
					<v-card-text> Forks: {{ proj.forkCount }} </v-card-text>
					<v-card-actions>
						<v-btn outlined rounded color="blue" text :to="{ name: 'Result', params: { id: proj.id } }">
							Ergebnis
						</v-btn>
					</v-card-actions>
				</v-card>
			</v-col>
		</v-row>
	</div>
</template>

<script>
import ax from "@/api";

export default {
	name: "Projects",
	data() {
		return {
			projects: [],
		};
	},
	mounted() {
		ax.get("/projects")
			.then((res) => {
				this.projects = res.data;
			})
			.catch((err) => {
				this.$em.emit("error", err);
			});
	},
};
</script>
