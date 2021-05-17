<template>
	<v-container>
		{{ project.name }} - zuletzt gelinted: {{ lastLint }}
		<v-list>
			<v-progress-linear :value="successPercentage" color="blue" height="10"></v-progress-linear>

			<v-list-item v-for="res in project.lintingResults[0].checkResults" :key="res.id">
				<v-list-item-avatar>
					<v-icon
						dark
						:class="{
							red: res.severity == 'HIGH',
							yellow: res.severity == 'MEDIUM',
							green: res.severity == 'LOW',
							blue: res.severity == 'NOT_SPECIFIED',
						}"
					>
						{{ res.result ? "mdi-check" : "mdi-close" }}
					</v-icon>
				</v-list-item-avatar>

				<v-list-item-content>
					<v-list-item-title v-text="res.checkName"></v-list-item-title>
					<v-list-item-subtitle v-text="res.description"></v-list-item-subtitle>
				</v-list-item-content>

				<v-list-item-action>
					<v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
							<v-btn icon v-bind="attrs" v-on="on">
								<v-icon color="grey lighten-1">mdi-information</v-icon>
							</v-btn>
						</template>
						<span>{{ res.fix }}</span>
					</v-tooltip>
				</v-list-item-action>
			</v-list-item>
		</v-list>
	</v-container>
</template>

<script>
import ax from "@/api";
import "dayjs";
import dayjs from "dayjs";

export default {
	name: "Result",
	data() {
		return {
			project: {
				lintingResults: [
					{
						checkResults: [{}],
					},
				],
			},
		};
	},
	computed: {
		lastLint() {
			return dayjs(this.project.lintingResults[0].lintTime).format("DD.MM.YYYY - H:mm");
		},
		successPercentage() {
			let succChecks = this.project.lintingResults[0].checkResults.filter((res) => {
				return res.result == true;
			});
			return (100 * succChecks.length) / this.project.lintingResults[0].checkResults.length;
		},
	},
	mounted() {
		ax.get(`/project/${this.$route.params.id}`)
			.then((res) => {
				this.project = res.data;
			})
			.catch((err) => {
				this.$em.emit("error", err);
			});
	},
};
</script>
